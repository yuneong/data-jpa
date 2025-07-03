package study.data_jpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.data_jpa.dto.MemberDto;
import study.data_jpa.entity.Member;
import study.data_jpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;
    
    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberQueryRepository memberQueryRepository;

    @BeforeEach
    void clean() {
        memberRepository.deleteAll(); // 각 테스트 실행 전에 데이터 삭제
    }

    @Test
    public void testMember() {
        System.out.println("memberRepository:: " + memberRepository.getClass());

        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

//        Optional<Member> findMember = memberRepository.findById(savedMember.getId());
        Member findMember = memberRepository.findById(savedMember.getId()).get();

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member);
    }

    @Test
    public void basicCRUD() {
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);

        long deletedCount = memberRepository.count();
        Assertions.assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);

        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        Assertions.assertThat(result.get(0).getAge()).isEqualTo(20);
        Assertions.assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void findTop3HelloBy() {
        List<Member> top3HelloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void namedQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findByUsername("AAA");
        Member findMember = result.get(0);
        Assertions.assertThat(findMember).isEqualTo(m1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> result = memberRepository.findUser("AAA", 10);
        Assertions.assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<String> usernameList = memberRepository.findUsernameList();
        for (String username : usernameList) {
            System.out.println("username = " + username);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);

        List<MemberDto> memberDto = memberRepository.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    public void findByNames() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> MemberList = memberRepository.findByNames(Arrays.asList("AAA", "BBB"));
        for (Member member : MemberList) {
            System.out.println("member = " + member);
        }
    }

    @Test
    public void returnType() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);

        List<Member> findList = memberRepository.findListByUsername("AAA");
        System.out.println(findList);

        Member findMember = memberRepository.findMemberByUsername("AAA");
        System.out.println(findMember);

        Optional<Member> findOptional = memberRepository.findOptionalByUsername("AAA");
        System.out.println(findOptional);
    }

    @Test
    public void paging() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // 반환시에 Member 객체로 하면 안되기 때문에 map을 사용해서 dto로 변환 후 리턴 필수!
        Page<MemberDto> toMap = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        // then
        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements(); // = totalCount

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println("totalElements = " + totalElements);

        Assertions.assertThat(content.size()).isEqualTo(3);
        Assertions.assertThat(totalElements).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void slice() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 10));
        memberRepository.save(new Member("member3", 10));
        memberRepository.save(new Member("member4", 10));
        memberRepository.save(new Member("member5", 10));

        int age = 10;
        // slice는 pageSize에 +1해서 쿼리 날림
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Slice<Member> page = memberRepository.findSliceByAge(age, pageRequest);

        // then
        List<Member> content = page.getContent();

        // slice는 totalPage를 조회하지 않아 아래 주석된 코드는 조회 불가능
        Assertions.assertThat(content.size()).isEqualTo(3);
//        Assertions.assertThat(totalElements).isEqualTo(5);
        Assertions.assertThat(page.getNumber()).isEqualTo(0);
//        Assertions.assertThat(page.getTotalPages()).isEqualTo(2);
        Assertions.assertThat(page.isFirst()).isTrue();
        Assertions.assertThat(page.hasNext()).isTrue();
    }

    @Test
    public void bulkUpdate() {
        // given
        memberRepository.save(new Member("member1", 10));
        memberRepository.save(new Member("member2", 19));
        memberRepository.save(new Member("member3", 20));
        memberRepository.save(new Member("member4", 21));
        memberRepository.save(new Member("member5", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);
        // persistenceContext 문제로 인해 아래 두 줄 사용함
//        em.flush();
//        em.clear(); // @Modifying(clearAutomatically = true)로 대신 사용 가능

        // db 에는 age=41 이지만 persistenceContext 에는 age=40으로 되어있음
        // jpa bulk 연산시 위와 같은 persistenceContext 문제 발생
        List<Member> memberList = memberRepository.findByUsername("member5");
        Member member5 = memberList.get(0);
        System.out.println("member5 = " + member5);

        // then
        Assertions.assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() {
        // given
        // member1 -> teamA
        // member2 -> teamB

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        Team teamC = new Team("teamC");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        teamRepository.save(teamC);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamB);
        Member member3 = new Member("member2", 10, teamC);
        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);

        em.flush();
        em.clear();

        // when (N + 1 문제)
        // select Member 1
//        List<Member> members = memberRepository.findAll(); // select Member
//        List<Member> members = memberRepository.findMemberFetchJoin(); // N+1 문제를 해결하는 fetch join -> 연관된 테이블을 한번에 가져옴
//        List<Member> members = memberRepository.findMemberEntityGraph();
        List<Member> members = memberRepository.findEntityGraphByUsername("member2");

        // then
        for (Member member : members) {
            System.out.println("@ member = " + member.getUsername());
            System.out.println("@@ member.teamClass = " + member.getTeam().getClass());
            System.out.println("@@@ member.team = " + member.getTeam().getName()); // select Team
        }
    }

    @Test
    public void queryHint() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
//        Member findMember = memberRepository.findById(member1.getId()).get();
//        findMember.setUsername("member2");
//        em.flush(); // 변경 감지하여 update 쿼리 실행

        Member findMember = memberRepository.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush(); // readOnly로 인해 변경 감지 안함
    }

    @Test
    public void lock() {
        // given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        // when
        List<Member> result = memberRepository.findLockByUsername("member1"); // select for update
    }

    @Test
    public void callCustom() {
//        List<Member> result = memberRepository.findMemberCustom();
        List<Member> result = memberQueryRepository.findAllMembers();
    }

    @Test
    void specBasic() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        Specification<Member> spec = MemberSpec.username("member1").and(MemberSpec.teamName("teamA"));
        List<Member> result = memberRepository.findAll(spec);

        // then
        Assertions.assertThat(result.size()).isEqualTo(1);
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("member1");
    }

    @Test
    void queryByExample() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        // Probe 생성
        Member member = new Member("member1");
        Team team = new Team("teamA"); // inner join 가능
        member.setTeam(team);

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("age"); // age에 대해서는 무시하고 조건으로 사용하지 말라는 조건

        Example<Member> example = Example.of(member, matcher);

        List<Member> result = memberRepository.findAll(example);

        // then
        Assertions.assertThat(result.get(0).getUsername()).isEqualTo("member1");
    }

    @Test
    void projections() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        List<NestedClosedProjections> result = memberRepository.findProjectionsByUsername("member1", NestedClosedProjections.class);

        // then
        for (NestedClosedProjections nestedClosedProjections : result) {
            System.out.println("username = " + nestedClosedProjections.getUsername());
            System.out.println("teamName = " + nestedClosedProjections.getTeam().getName());
        }
    }

    @Test
    void nativeQuery() {
        // given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 10, teamA);
        em.persist(member1);
        em.persist(member2);

        em.flush();
        em.clear();

        // when
        Member result = memberRepository.findByNativeQuery("member1");
        System.out.println("result = " + result);
    }

}