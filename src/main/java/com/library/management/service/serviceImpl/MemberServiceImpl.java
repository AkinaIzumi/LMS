package com.library.management.service.serviceImpl;

import com.library.management.entity.Member;
import com.library.management.repository.MemberRepository;
import com.library.management.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    public Optional<Member> getMemberById(Integer id) {
        return memberRepository.findById(id);
    }

    @Override
    public Optional<Member> getMemberByCode(String memberCode) {
        return memberRepository.findByMemberCode(memberCode);
    }

    @Override
    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public Member updateMember(Integer id, Member memberDetails) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));

        member.setMemberCode(memberDetails.getMemberCode());
        member.setName(memberDetails.getName());
        member.setPhone(memberDetails.getPhone());
        member.setEmail(memberDetails.getEmail());
        member.setAddress(memberDetails.getAddress());
        member.setMemberType(memberDetails.getMemberType());
        member.setStatus(memberDetails.getStatus());
        member.setRegisteredDate(memberDetails.getRegisteredDate());

        return memberRepository.save(member);
    }

    @Override
    public void deleteMember(Integer id) {
        memberRepository.deleteById(id);
    }
}