//package com.example.springservice.repo;
//
//import com.example.springservice.entites.ArtistStyleMapping;
//import com.example.springservice.entites.enmap.ArtistStyle;
//import com.example.springservice.entites.enmap.ArtistStyleMappingId;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface ArtistStyleRepository extends JpaRepository<ArtistStyle, Integer> {
//    Optional<ArtistStyle> findByStyleNameIgnoreCase(String name);
//    @Query("SELECT m FROM ArtistStyleMapping m WHERE m.user.userId = :userId")
//    List<ArtistStyleMapping> findMappingsByUserId(@Param("userId") Integer userId);
//
//}
//
