package com.manoj.finorder.payledgerservice.repository;

import com.manoj.finorder.payledgerservice.model.LedgerEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;

public interface LedgerEntryRepository extends MongoRepository<LedgerEntry, String> {
}
