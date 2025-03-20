package com.prgrms.be.intermark.domain.newerd.castinginfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prgrms.be.intermark.domain.newerd.castinginfo.model.CastingInfo;

@Repository
public interface CastingInfoRepositoryTobe extends JpaRepository<CastingInfo, Long> {
}
