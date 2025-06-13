package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

	private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
	private static final Font SECTION_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
	private static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
	private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 10);
	private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 8);

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final float PHOTO_WIDTH = 100f;
	private static final float PHOTO_HEIGHT = 120f;
	private static final BaseColor HEADER_BG_COLOR = new BaseColor(220, 220, 220);
	private static final BaseColor BORDER_COLOR = new BaseColor(200, 200, 200);

	public byte[] generateEmployeePdf(EmployeeDTO employee) throws DocumentException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Document document = new Document(PageSize.A4, 36, 36, 36, 36);

		try {
			PdfWriter writer = PdfWriter.getInstance(document, baos);
			document.open();

			addDocumentHeader(document, writer, employee);
			addPersonalInfoSection(document, employee);
			addAddressSection(document, employee);

			if (!employee.getEducationList().isEmpty()) {
				addEducationSection(document, employee.getEducationList());
			}

			if (!employee.getCertifications().isEmpty()) {
				addCertificationSection(document, employee.getCertifications());
			}

			if (!employee.getSkills().isEmpty()) {
				addSkillsSection(document, employee.getSkills());
			}

			if (!employee.getExperiences().isEmpty()) {
				addExperienceSection(document, employee.getExperiences());
			}

			addFooter(writer);

		} finally {
			if (document.isOpen()) {
				document.close();
			}
		}
		return baos.toByteArray();
	}

	private void addExperienceSection(Document document, List<ExperienceDTO> experiences) throws DocumentException {
		Paragraph sectionHeader = new Paragraph("Experience", SECTION_FONT);
		sectionHeader.setSpacingAfter(10f);
		document.add(sectionHeader);

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5f);
		table.setSpacingAfter(15f);

		// Table headers
		addTableHeaderCell(table, "Level");
		addTableHeaderCell(table, "Job Role");

		// Table rows
		for (ExperienceDTO experience : experiences) {
			addTableCell(table, experience.getLevel());
			addTableCell(table, experience.getJobRole());
		}

		document.add(table);
	}

	private void addDocumentHeader(Document document, PdfWriter writer, EmployeeDTO employee) throws DocumentException {
		PdfPTable headerTable = new PdfPTable(2);
		headerTable.setWidthPercentage(100);
		headerTable.setWidths(new float[] { 1, 3 });
		headerTable.setSpacingAfter(20f);

		// Photo cell (left column)
		PdfPCell photoCell = createPhotoCell(employee);
		photoCell.setPaddingRight(15f);
		headerTable.addCell(photoCell);

		// Info cell (right column)
		PdfPCell infoCell = createInfoCell(employee);
		infoCell.setPaddingLeft(15f);
		headerTable.addCell(infoCell);

		document.add(headerTable);

		// Add separator line
		Paragraph separator = new Paragraph();
		separator.add(new Chunk(new LineSeparator(1f, 100f, BORDER_COLOR, Element.ALIGN_CENTER, -1)));
		separator.setSpacingAfter(15f);
		document.add(separator);
	}

	private PdfPCell createPhotoCell(EmployeeDTO employee) {
		PdfPCell photoCell = new PdfPCell();
		photoCell.setBorder(Rectangle.NO_BORDER);
		photoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		photoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		photoCell.setPadding(5f);
		photoCell.setFixedHeight(PHOTO_HEIGHT + 25);

		try {
			if (employee.getProfilePhoto() != null && employee.getProfilePhoto().getData() != null
					&& employee.getProfilePhoto().getData().length > 0) {

				// Create photo container table
				PdfPTable photoTable = new PdfPTable(1);
				photoTable.setWidthPercentage(100);

				// Add photo
				Image photo = Image.getInstance(employee.getProfilePhoto().getData());
				photo.scaleToFit(PHOTO_WIDTH, PHOTO_HEIGHT);
				photo.setBorder(Rectangle.BOX);
				photo.setBorderWidth(1f);
				photo.setBorderColor(BaseColor.LIGHT_GRAY);

				PdfPCell imageCell = new PdfPCell();
				imageCell.addElement(photo);
				imageCell.setBorder(Rectangle.NO_BORDER);
				imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				photoTable.addCell(imageCell);

				// Add caption
				PdfPCell captionCell = new PdfPCell(new Phrase("Employee Photo", SMALL_FONT));
				captionCell.setBorder(Rectangle.NO_BORDER);
				captionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				photoTable.addCell(captionCell);

				photoCell.addElement(photoTable);

			} else {
				// Create placeholder table
				PdfPTable placeholderTable = new PdfPTable(1);
				placeholderTable.setWidthPercentage(100);

				// Add "No Photo Available" text
				PdfPCell placeholderCell = new PdfPCell(new Phrase("No Photo Available", NORMAL_FONT));
				placeholderCell.setFixedHeight(PHOTO_HEIGHT);
				placeholderCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
				placeholderCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				placeholderCell.setBorder(Rectangle.BOX);
				placeholderCell.setBorderWidth(1f);
				placeholderCell.setBorderColor(BaseColor.LIGHT_GRAY);
				placeholderTable.addCell(placeholderCell);

				// Add caption
				PdfPCell captionCell = new PdfPCell(new Phrase("Employee Photo", SMALL_FONT));
				captionCell.setBorder(Rectangle.NO_BORDER);
				captionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				placeholderTable.addCell(captionCell);

				photoCell.addElement(placeholderTable);
			}
		} catch (Exception e) {
			// Fallback placeholder if error occurs
			PdfPTable errorTable = new PdfPTable(1);
			PdfPCell errorCell = new PdfPCell(new Phrase("Photo Error", NORMAL_FONT));
			errorCell.setFixedHeight(PHOTO_HEIGHT);
			errorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			errorCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			errorCell.setBorder(Rectangle.BOX);
			errorCell.setBorderColor(BaseColor.RED);
			errorTable.addCell(errorCell);

			PdfPCell captionCell = new PdfPCell(new Phrase("Employee Photo", SMALL_FONT));
			captionCell.setBorder(Rectangle.NO_BORDER);
			captionCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			errorTable.addCell(captionCell);

			photoCell.addElement(errorTable);
		}
		return photoCell;
	}

	private void addNoPhotoPlaceholder(PdfPCell cell) {
		Paragraph placeholder = new Paragraph("No Photo Available", NORMAL_FONT);
		placeholder.setAlignment(Element.ALIGN_CENTER);
		cell.addElement(placeholder);
	}

	private PdfPCell createInfoCell(EmployeeDTO employee) {
		PdfPCell infoCell = new PdfPCell();
		infoCell.setBorder(Rectangle.NO_BORDER);
		infoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
		infoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

		// Add title
		Paragraph title = new Paragraph("EMPLOYEE PROFILE", TITLE_FONT);
		title.setAlignment(Element.ALIGN_LEFT);
		title.setSpacingAfter(10f);
		infoCell.addElement(title);

		// Add basic info
		PdfPTable infoTable = new PdfPTable(2);
		infoTable.setWidthPercentage(90);
		infoTable.setHorizontalAlignment(Element.ALIGN_LEFT);

		addTableRow(infoTable, "Employee ID:", employee.getEmployeeId().toString());
		addTableRow(infoTable, "Name:", employee.getFirstName() + " " + employee.getLastName());
		addTableRow(infoTable, "Date of Birth:",
				employee.getDob() != null ? employee.getDob().format(DATE_FORMATTER) : "N/A");

		infoCell.addElement(infoTable);

		return infoCell;
	}

	private void addPersonalInfoSection(Document document, EmployeeDTO employee) throws DocumentException {
		Paragraph sectionHeader = new Paragraph("Personal Information", SECTION_FONT);
		sectionHeader.setSpacingAfter(10f);
		document.add(sectionHeader);

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5f);
		table.setSpacingAfter(15f);

		addTableRow(table, "Gender:", employee.getGender());
		addTableRow(table, "Email:", employee.getEmail());
		addTableRow(table, "Personal Email:", employee.getPersonalEmail());
		addTableRow(table, "Father's Name:", employee.getFatherName());
		addTableRow(table, "Mobile:", employee.getMobile());
		addTableRow(table, "Role:", employee.getRole() != null ? employee.getRole().name() : "N/A");
		document.add(table);
	}

	private void addAddressSection(Document document, EmployeeDTO employee) throws DocumentException {
		Paragraph sectionHeader = new Paragraph("Address Information", SECTION_FONT);
		sectionHeader.setSpacingAfter(10f);
		document.add(sectionHeader);

		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5f);
		table.setSpacingAfter(15f);

		// Present Address
		addTableRow(table, "Present Address:", formatAddress(employee.getPresentStreet(), employee.getPresentCity(),
				employee.getPresentState(), employee.getPresentZip()));

		// Permanent Address
		addTableRow(table, "Permanent Address:", formatAddress(employee.getPermanentStreet(),
				employee.getPermanentCity(), employee.getPermanentState(), employee.getPermanentZip()));

		document.add(table);
	}

	private String formatAddress(String street, String city, String state, String zip) {
		StringBuilder sb = new StringBuilder();
		if (street != null && !street.isEmpty())
			sb.append(street).append("\n");
		if (city != null && !city.isEmpty())
			sb.append(city);
		if (state != null && !state.isEmpty())
			sb.append(", ").append(state);
		if (zip != null && !zip.isEmpty())
			sb.append(" ").append(zip);
		return sb.toString();
	}

	private void addEducationSection(Document document, List<EducationDTO> educationList) throws DocumentException {
		Paragraph sectionHeader = new Paragraph("Education", SECTION_FONT);
		sectionHeader.setSpacingAfter(10f);
		document.add(sectionHeader);

		PdfPTable table = new PdfPTable(4);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5f);
		table.setSpacingAfter(15f);

		// Table headers
		addTableHeaderCell(table, "Education");
		addTableHeaderCell(table, "Institution");
		addTableHeaderCell(table, "Year");
		addTableHeaderCell(table, "Percentage");

		// Table rows
		for (EducationDTO education : educationList) {
			addTableCell(table, education.getEducationName());
			addTableCell(table, education.getCollege());
			addTableCell(table, education.getYear());
			addTableCell(table, education.getPercentage() != null ? education.getPercentage() + "%" : "N/A");
		}

		document.add(table);
	}

	private void addCertificationSection(Document document, List<CertificationDTO> certifications)
			throws DocumentException {
		Paragraph sectionHeader = new Paragraph("Certifications", SECTION_FONT);
		sectionHeader.setSpacingAfter(10f);
		document.add(sectionHeader);

		PdfPTable table = new PdfPTable(3);
		table.setWidthPercentage(100);
		table.setSpacingBefore(5f);
		table.setSpacingAfter(15f);

		// Table headers
		addTableHeaderCell(table, "Name");
		addTableHeaderCell(table, "Organization");
		addTableHeaderCell(table, "Date");

		// Table rows
		for (CertificationDTO certification : certifications) {
			addTableCell(table, certification.getName());
			addTableCell(table, certification.getOrganization());
			addTableCell(table,
					certification.getDate() != null ? certification.getDate().format(DATE_FORMATTER) : "N/A");
		}

		document.add(table);
	}

	private void addSkillsSection(Document document, List<SkillDTO> skills) throws DocumentException {
		Paragraph sectionHeader = new Paragraph("Skills", SECTION_FONT);
		sectionHeader.setSpacingAfter(10f);
		document.add(sectionHeader);

		// Create a bulleted list for skills
		com.itextpdf.text.List list = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
		list.setListSymbol("\u2022"); // Bullet point
		list.setIndentationLeft(20f);

		for (SkillDTO skill : skills) {
			ListItem item = new ListItem(skill.getSkill(), NORMAL_FONT);
			list.add(item);
		}

		document.add(list);
	}

	private void addFooter(PdfWriter writer) {
		PdfPTable footer = new PdfPTable(1);
		footer.setTotalWidth(PageSize.A4.getWidth() - 72); // Account for margins
		footer.setSpacingBefore(20f);

		PdfPCell cell = new PdfPCell(new Phrase("Generated by Employee Management System", SMALL_FONT));
		cell.setBorder(Rectangle.TOP);
		cell.setBorderColor(BORDER_COLOR);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPaddingTop(5f);
		footer.addCell(cell);

		footer.writeSelectedRows(0, -1, 36, 30, writer.getDirectContent());
	}

	private void addTableRow(PdfPTable table, String label, String value) {
		addTableCell(table, label, true);
		addTableCell(table, value);
	}

	private void addTableCell(PdfPTable table, String text) {
		addTableCell(table, text, false);
	}

	private void addTableCell(PdfPTable table, String text, boolean isHeader) {
		PdfPCell cell = new PdfPCell(new Phrase(text, isHeader ? HEADER_FONT : NORMAL_FONT));
		cell.setBorderWidth(0.5f);
		cell.setBorderColor(BORDER_COLOR);
		cell.setPadding(5f);
		cell.setPaddingLeft(8f);
		table.addCell(cell);
	}

	private void addTableHeaderCell(PdfPTable table, String text) {
		PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
		cell.setBackgroundColor(HEADER_BG_COLOR);
		cell.setBorderWidth(0.5f);
		cell.setBorderColor(BORDER_COLOR);
		cell.setPadding(5f);
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(cell);
	}
}
