package com.constellio.model.services.records;

import com.constellio.data.utils.KeyListMap;
import com.constellio.model.entities.records.Record;
import com.constellio.model.entities.records.Transaction;
import com.constellio.model.entities.security.TransactionSecurityModel;

import java.util.HashMap;
import java.util.Map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionExecutionContext {

	Map<String, KeyListMap<String, Record>> metadatasInvertedAggregatedValuesMap = new HashMap<>();

	Transaction transaction;

	TransactionSecurityModel transactionSecurityModel;

	public TransactionExecutionContext(Transaction transaction) {
		this.transaction = transaction;
	}


	public Transaction getTransaction() {
		return transaction;
	}

	public TransactionSecurityModel getTransactionSecurityModel() {
		return transactionSecurityModel;
	}

	public TransactionExecutionContext setTransactionSecurityModel(
			TransactionSecurityModel transactionSecurityModel) {
		this.transactionSecurityModel = transactionSecurityModel;
		return this;
	}
}
