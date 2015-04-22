/*Constellio Enterprise Information Management

Copyright (c) 2015 "Constellio inc."

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package com.constellio.app.modules.rm.reports.builders.labels;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.constellio.app.modules.rm.reports.PdfTableUtils;
import com.constellio.app.modules.rm.reports.model.labels.LabelsReportField;
import com.constellio.app.modules.rm.reports.model.labels.LabelsReportLabel;
import com.constellio.app.modules.rm.reports.model.labels.LabelsReportLayout;
import com.constellio.app.modules.rm.reports.model.labels.LabelsReportModel;
import com.constellio.app.reports.builders.administration.plan.ReportBuilder;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class LabelsReportBuilder implements ReportBuilder {

	private LabelsReportModel model;
	private PdfTableUtils tableUtils;

	public LabelsReportBuilder(LabelsReportModel model) {
		this.model = model;
		this.tableUtils = new PdfTableUtils();
	}

	public String getFileExtension() {
		return "pdf";
	}

	public void build(OutputStream output) throws IOException {
		LabelsReportLayout layout = model.getLayout();
		Document document = new Document(layout.getPageSize(), layout.getLeftMargin(), layout.getRightMargin(),
				layout.getTopMargin(), layout.getBottomMargin());
		try {
			PdfWriter.getInstance(document, output);
			document.open();
			document.add(createPrintableLabels(layout));
			document.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private PdfPTable createPrintableLabels(LabelsReportLayout layout) {

		PdfPTable labels = new PdfPTable(layout.getNumberOfColumns());
		labels.setWidthPercentage(100f);

		float labelHeight = getLabelHeight(layout);

		float labelWidth = getLabelWidth(layout);

		for (LabelsReportLabel label : model.getLabelsReportLabels()) {
			labels.addCell(createPrintableLabel(label, labelWidth, labelHeight));
		}

		addExtraEmptyLabelIfOddNumberOfLabels(labels);

		return labels;
	}

	private float getLabelHeight(LabelsReportLayout layout) {
		float totalTopBottomMargins = layout.getTopMargin() + layout.getBottomMargin();
		float labelHeight = (layout.getPageSize().getHeight() - totalTopBottomMargins) / layout.getNumberOfRows();
		return labelHeight;
	}

	private float getLabelWidth(LabelsReportLayout layout) {
		float totalSideMargins = layout.getLeftMargin() + layout.getRightMargin();
		float labelWidth = (layout.getPageSize().getWidth() - totalSideMargins) / layout.getNumberOfColumns();
		return labelWidth;
	}

	private PdfPCell createPrintableLabel(LabelsReportLabel label, float labelWidth, float labelHeight) {
		int numColumns = (int) Math.ceil(labelWidth / 10);

		PdfPTable printableLabel = new PdfPTable(numColumns);
		printableLabel.getDefaultCell().setBorder(Rectangle.NO_BORDER);

		int numRows = approximateNumberOfRowsBasedOnHeight10PerRow(labelHeight);

		float rowHeight = calculateExactRowHeight(labelHeight, numRows);

		for (int rowNumber = 0; rowNumber < numRows; rowNumber++) {
			List<LabelsReportField> fields = label.getFieldsInRow(rowNumber);
			if (fields.isEmpty()) {
				tableUtils.addEmptyRows(printableLabel, 1, rowHeight);
			} else {
				addRowWithFields(printableLabel, rowHeight, fields);
			}
		}

		PdfPCell printableLabelField = new PdfPCell(printableLabel);
		printableLabelField.setBorder(Rectangle.NO_BORDER);
		removeBordersIfLabelIsBlank(label, printableLabelField);
		printableLabelField.setUseAscender(true);
		return printableLabelField;
	}

	private int approximateNumberOfRowsBasedOnHeight10PerRow(float labelHeight) {
		return (int) Math.floor(labelHeight / 10);
	}

	float calculateExactRowHeight(float labelHeight, int numRows) {
		return labelHeight / numRows;
	}

	private void removeBordersIfLabelIsBlank(LabelsReportLabel label, PdfPCell printableLabelField) {
		if (label.getFields().isEmpty()) {
			printableLabelField.setBorder(Rectangle.NO_BORDER);
		}
	}

	private void addRowWithFields(PdfPTable printableLabel, float rowHeight, List<LabelsReportField> fields) {
		Iterator<LabelsReportField> fieldsIterator = fields.iterator();
		int lastFieldEnd = 0;
		while (fieldsIterator.hasNext()) {
			LabelsReportField field = fieldsIterator.next();

			tableUtils.addEmptyCells(printableLabel, field.positionX - lastFieldEnd, rowHeight);

			printableLabel.addCell(createLabelField(field, rowHeight));

			lastFieldEnd = field.positionX + field.width;
		}
		printableLabel.completeRow();
	}

	private PdfPCell createLabelField(LabelsReportField field, float rowHeight) {
		Phrase phrase = new Phrase(field.getValue(), field.getFont().getFont());
		PdfPCell fieldCell = new PdfPCell(phrase);
		fieldCell.setColspan(field.width);
		fieldCell.setRowspan(field.height);
		fieldCell.setFixedHeight(rowHeight);
		fieldCell.setBorder(Rectangle.NO_BORDER);
		return fieldCell;
	}

	private void addExtraEmptyLabelIfOddNumberOfLabels(PdfPTable labels) {
		if (model.getLabelsReportLabels().size() % 2 == 1) {
			PdfPCell emptyCell = labels.getDefaultCell();
			emptyCell.setBorder(Rectangle.NO_BORDER);
			labels.addCell(emptyCell);
		}
	}
};
