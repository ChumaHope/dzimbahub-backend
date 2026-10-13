package zw.ac.msu.dzimbahub.repository;
import org.springframework.data.jpa.repository.JpaRepository; import zw.ac.msu.dzimbahub.model.DemoRecord;
public interface DemoRecordRepository extends JpaRepository<DemoRecord,Long>{long countByType(String type);}
