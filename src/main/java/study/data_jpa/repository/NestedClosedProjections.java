package study.data_jpa.repository;

public interface NestedClosedProjections { // 중첩 구조

    String getUsername(); // 프로젝션 대상 첫번째(root entity)는 최적화 가능
    TeamInfo getTeam(); // 두번째(root entity 아님) 부터는 객체로 조회 (left outer join 처리)

    interface TeamInfo {
        String getName();
    }
}
