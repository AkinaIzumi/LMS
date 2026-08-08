package com.library.management.service.serviceImpl;

import com.library.management.entity.Member;
import com.library.management.repository.MemberRepository;
import com.library.management.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Member> getMemberById(Integer id) {
        return memberRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
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

        member.setName(memberDetails.getName());
        member.setPhone(memberDetails.getPhone());
        member.setEmail(memberDetails.getEmail());
        member.setAddress(memberDetails.getAddress());
        member.setMemberType(memberDetails.getMemberType());
        member.setRegisteredDate(memberDetails.getRegisteredDate());
        member.setExpiredDate(memberDetails.getExpiredDate());

        if (memberDetails.getLossBookCount() != null) {
            int lossCount = memberDetails.getLossBookCount();
            member.setLossBookCount(lossCount);

            LocalDate today = LocalDate.now();
            boolean isExpired = memberDetails.getExpiredDate() != null && memberDetails.getExpiredDate().isBefore(today);

            if (isExpired || lossCount >= 2) {
                member.setStatus("banned");
            } else if (lossCount == 1) {
                member.setStatus("warning");
            } else {
                member.setStatus("active");
            }
        } else {
    
            member.setStatus(memberDetails.getStatus());
        }

        return memberRepository.save(member);
    }

    @Override
    public void deleteMember(Integer id) {
        memberRepository.deleteById(id);
    }
}