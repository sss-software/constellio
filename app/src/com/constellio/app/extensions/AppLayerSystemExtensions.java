package com.constellio.app.extensions;

import com.constellio.app.api.extensions.BaseWindowExtension;
import com.constellio.app.api.extensions.EmailExtension;
import com.constellio.app.api.extensions.PagesComponentsExtension;
import com.constellio.app.api.extensions.UpdateModeExtension;
import com.constellio.app.api.extensions.params.BaseWindowParams;
import com.constellio.app.api.extensions.params.DecorateMainComponentAfterInitExtensionParams;
import com.constellio.app.api.extensions.params.EmailMessageParams;
import com.constellio.app.api.extensions.params.PagesComponentsExtensionParams;
import com.constellio.app.extensions.api.GlobalGroupExtension;
import com.constellio.app.extensions.api.GlobalGroupExtension.GlobalGroupExtensionActionPossibleParams;
import com.constellio.app.extensions.api.UserCredentialExtension;
import com.constellio.app.extensions.api.UserCredentialExtension.UserCredentialExtensionActionPossibleParams;
import com.constellio.app.extensions.sequence.AvailableSequence;
import com.constellio.app.extensions.sequence.AvailableSequenceForSystemParams;
import com.constellio.app.extensions.sequence.SystemSequenceExtension;
import com.constellio.app.extensions.ui.ConstellioUIExtention;
import com.constellio.app.extensions.ui.ConstellioUIExtention.ConstellioUIExtentionParams;
import com.constellio.app.ui.application.ConstellioUI;
import com.constellio.data.frameworks.extensions.VaultBehaviorsList;
import com.constellio.model.entities.records.wrappers.User;
import com.constellio.model.entities.security.global.GlobalGroup;
import com.constellio.model.entities.security.global.UserCredential;
import com.constellio.model.services.emails.EmailServices.EmailMessage;

import java.util.ArrayList;
import java.util.List;

public class AppLayerSystemExtensions {

	public VaultBehaviorsList<PagesComponentsExtension> pagesComponentsExtensions = new VaultBehaviorsList<>();

	public VaultBehaviorsList<SystemSequenceExtension> systemSequenceExtensions = new VaultBehaviorsList<>();

	public VaultBehaviorsList<EmailExtension> emailExtensions = new VaultBehaviorsList<>();

	public VaultBehaviorsList<BaseWindowExtension> windowExtensions = new VaultBehaviorsList<>();

	public VaultBehaviorsList<GlobalGroupExtension> globalGroupExtensions = new VaultBehaviorsList<>();

	public VaultBehaviorsList<UserCredentialExtension> userCredentialGroupExtensions = new VaultBehaviorsList<>();

	public VaultBehaviorsList<ConstellioUIExtention> constellioUIExtentions = new VaultBehaviorsList<>();

	public List<AvailableSequence> getAvailableSequences() {

		AvailableSequenceForSystemParams params = new AvailableSequenceForSystemParams();

		List<AvailableSequence> availableSequences = new ArrayList<>();

		for (SystemSequenceExtension extension : systemSequenceExtensions) {
			List<AvailableSequence> extensionSequences = extension.getAvailableSequences(params);
			if (extensionSequences != null) {
				availableSequences.addAll(extensionSequences);
			}
		}

		return availableSequences;
	}

	public void decorateView(PagesComponentsExtensionParams params) {
		for (PagesComponentsExtension extension : pagesComponentsExtensions) {
			extension.decorateView(params);
		}
	}

	public void decorateMainComponentBeforeViewInstanciated(DecorateMainComponentAfterInitExtensionParams params) {
		for (PagesComponentsExtension extension : pagesComponentsExtensions) {
			extension.decorateMainComponentBeforeViewInstanciated(params);
		}
	}

	public void decorateMainComponentAfterViewAssembledOnViewEntered(
			DecorateMainComponentAfterInitExtensionParams params) {
		for (PagesComponentsExtension extension : pagesComponentsExtensions) {
			extension.decorateMainComponentAfterViewAssembledOnViewEntered(params);
		}
	}

	public void decorateMainComponentBeforeViewAssembledOnViewEntered(
			DecorateMainComponentAfterInitExtensionParams params) {
		for (PagesComponentsExtension extension : pagesComponentsExtensions) {
			extension.decorateMainComponentBeforeViewAssembledOnViewEntered(params);
		}
	}

	public UpdateModeExtension alternateUpdateMode = new UpdateModeExtension();

	public EmailMessage newEmailMessage(EmailMessageParams params) {
		EmailMessage emailMessage = null;
		for (EmailExtension emailExtension : emailExtensions) {
			emailMessage = emailExtension.newEmailMessage(params);
			if (emailMessage != null) {
				break;
			}
		}
		return emailMessage;
	}
	
	public void decorateWindow(BaseWindowParams params) {
		for (BaseWindowExtension windowExtension : windowExtensions) {
			windowExtension.decorateWindow(params);
		}
	}

	public boolean isAddSubGroupActionPossibleOnGlobalGroup(final GlobalGroup globalGroup, final User user) {
		return globalGroupExtensions.getBooleanValue(true,
				(behavior) -> behavior.isAddSubGroupActionPossible(
						new GlobalGroupExtensionActionPossibleParams(globalGroup, user)));
	}

	public boolean isEditActionPossibleOnGlobalGroup(final GlobalGroup globalGroup, final User user) {
		return globalGroupExtensions.getBooleanValue(true,
				(behavior) -> behavior.isEditActionPossible(
						new GlobalGroupExtensionActionPossibleParams(globalGroup, user)));
	}

	public boolean isDeleteActionPossibleOnGlobalGroup(final GlobalGroup globalGroup, final User user) {
		return globalGroupExtensions.getBooleanValue(true,
				(behavior) -> behavior.isDeleteActionPossible(
						new GlobalGroupExtensionActionPossibleParams(globalGroup, user)));
	}

	public boolean isEditActionPossibleOnUserCredential(final UserCredential userCredential, final User user) {
		return userCredentialGroupExtensions.getBooleanValue(true,
				(behavior) -> behavior.isEditActionPossible(
						new UserCredentialExtensionActionPossibleParams(userCredential, user)));
	}

	public boolean isGenerateTokenActionPossibleOnUserCredential(final UserCredential userCredential, final User user) {
		return userCredentialGroupExtensions.getBooleanValue(true,
				(behavior) -> behavior.isGenerateTokenActionPossible(
						new UserCredentialExtensionActionPossibleParams(userCredential, user)));
	}

	public void addToConstellioUIInitialisation(ConstellioUI constellioUI) {
		for (ConstellioUIExtention constellioUIExtention : constellioUIExtentions) {
			constellioUIExtention.addToInitialisation(new ConstellioUIExtentionParams(constellioUI));
		}
	}
}
