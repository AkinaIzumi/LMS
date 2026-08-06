package com.library.management.service;

import com.library.management.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    
    List<Member> getAllMembers();

    Optional<Member> getMemberById(Integer id);

    Optional<Member> getMemberByCode(String memberCode);

    Member createMember(Member member);

    Member updateMember(Integer id, Member memberDetails);

    void deleteMember(Integer id);
}