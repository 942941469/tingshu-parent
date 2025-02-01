package com.atguigu.tingshu.search.repository;

import com.atguigu.tingshu.model.search.AlbumInfoIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * @author admin
 * @version 1.0
 */
public interface AlbumInfoIndexRepository extends ElasticsearchRepository<AlbumInfoIndex, Long> {
    List<AlbumInfoIndex> searchByCategory3Id(Long category3Id);
}
