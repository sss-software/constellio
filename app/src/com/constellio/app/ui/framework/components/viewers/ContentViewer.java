package com.constellio.app.ui.framework.components.viewers;

import com.constellio.app.modules.rm.ui.entities.DocumentVO;
import com.constellio.app.modules.rm.wrappers.Document;
import com.constellio.app.ui.entities.ContentVersionVO;
import com.constellio.app.ui.entities.RecordVO;
import com.constellio.app.ui.framework.components.viewers.audio.AudioViewer;
import com.constellio.app.ui.framework.components.viewers.document.DocumentViewer;
import com.constellio.app.ui.framework.components.viewers.image.ImageViewer;
import com.constellio.app.ui.framework.components.viewers.video.VideoViewer;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ContentViewer extends CustomComponent {

	private Component viewerComponent;
	private List<VisibilityChangeListener> imageViewerVisibilityChangeListenerList;
	private boolean didVisibilityChangeWhileAttaching = false;

	public ContentViewer(RecordVO recordVO, String metadataCode, ContentVersionVO contentVersionVO) {
		imageViewerVisibilityChangeListenerList = new ArrayList<>();
		setId(UUID.randomUUID().toString());
		if (contentVersionVO != null) {
			String fileName = contentVersionVO.getFileName();
			String extension = StringUtils.lowerCase(FilenameUtils.getExtension(fileName));

			if (Arrays.asList(ImageViewer.SUPPORTED_EXTENSIONS).contains(extension)) {
				ImageViewer imageViewer = new ImageViewer(recordVO, Document.CONTENT, contentVersionVO) {
					@Override
					public void setVisible(boolean newVisibility) {
						boolean wasVisible = this.isVisible();
						super.setVisible(newVisibility);

						if (newVisibility != wasVisible) {
							fireImageViewerVisibilityChangeListerners(newVisibility);
						}
					}
				};

				viewerComponent = imageViewer;
			} else if (Arrays.asList(AudioViewer.SUPPORTED_EXTENSIONS).contains(extension)) {
				AudioViewer audioViewer = new AudioViewer(contentVersionVO);
				viewerComponent = audioViewer;
			} else if (Arrays.asList(VideoViewer.SUPPORTED_EXTENSIONS).contains(extension)) {
				VideoViewer videoViewer = new VideoViewer(contentVersionVO);
				viewerComponent = videoViewer;
			} else if (DocumentViewer.isSupported(fileName)) {
				DocumentViewer documentViewer;
				if (recordVO instanceof DocumentVO) {
					documentViewer = new DocumentViewer(recordVO, Document.CONTENT, contentVersionVO);
				} else if (metadataCode != null) {
					documentViewer = new DocumentViewer(recordVO, metadataCode, contentVersionVO);
				} else {
					documentViewer = null;
				}
				viewerComponent = documentViewer;
			}
		}
		if (viewerComponent == null) {
			setVisible(false);
		} else {
			setCompositionRoot(viewerComponent);
		}
	}

	public void refresh() {
		if (viewerComponent instanceof ImageViewer) {
			((ImageViewer) viewerComponent).show();
		}
	}

	protected void fireImageViewerVisibilityChangeListerners(boolean visiblility) {
		Iterator<VisibilityChangeListener> visibilityChangeListenerIterator = imageViewerVisibilityChangeListenerList.iterator();

		while (visibilityChangeListenerIterator.hasNext()) {
			VisibilityChangeEvent visibilityChangeEvent = new VisibilityChangeEvent(visiblility, false);
			VisibilityChangeListener visibilityChangeListener = visibilityChangeListenerIterator.next();

			visibilityChangeListener.onVisibilityChange(visibilityChangeEvent);

			if (visibilityChangeEvent.isRemoveThisVisiblityLisener()) {
				visibilityChangeListenerIterator.remove();
			}
		}
	}

	public void addImageViewerVisibilityChangeListener(VisibilityChangeListener visibilityChangeListener) {
		imageViewerVisibilityChangeListenerList.add(visibilityChangeListener);
	}

	public boolean isViewerComponentVisible() {
		return isVisible() && viewerComponent != null && viewerComponent.isVisible();
	}

	@Override
	public void setHeight(float height, Unit unit) {
		super.setHeight(height, unit);
		if (viewerComponent instanceof DocumentViewer) {
			viewerComponent.setHeight(height, unit);
		}
	}

	@Override
	public void attach() {
		super.attach();
//		if (ComponentTreeUtils.findParent(this, Window.class) != null) {
//			String js = "document.getElementById('" + getId() + "').style.position='fixed';";
//			js += "document.getElementById('" + getId() + "').style.width=calc(100% - 850px);";
//			JavaScript.getCurrent().execute(js);
//		}
	}

	public boolean isVerticalScroll() {
		return viewerComponent instanceof DocumentViewer;
	}

}
