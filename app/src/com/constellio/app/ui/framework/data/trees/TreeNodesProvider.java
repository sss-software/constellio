package com.constellio.app.ui.framework.data.trees;

import com.constellio.app.ui.framework.data.TreeNode;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;


public interface TreeNodesProvider<T extends Serializable> {

	TreeNodesProviderResponse<T> getNodes(String optionalParentId, int start, int maxSize, T fastContinuationInfos);

	class TreeNodesProviderResponse<T> {

		@Getter
		private boolean moreNodes;

		@Getter
		private List<TreeNode> nodes;

		@Getter
		private T fastContinuationInfos;

		public TreeNodesProviderResponse(boolean moreNodes, List<TreeNode> nodes, T fastContinuationInfos) {
			this.moreNodes = moreNodes;
			this.nodes = nodes;
			this.fastContinuationInfos = fastContinuationInfos;
		}

		public TreeNodesProviderResponse(boolean moreNodes, List<TreeNode> nodes) {
			this.moreNodes = moreNodes;
			this.nodes = nodes;
		}
	}

}
