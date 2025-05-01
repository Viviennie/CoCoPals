package com.tongji.codejourneycolab.codejourneycolabbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.DocumentInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.FileInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.entity.Document;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DocumentMapper extends BaseMapper<Document> {

    @Select("SELECT EXISTS(SELECT 1 FROM user_document WHERE user_id = #{userId} AND document_id = #{documentId})")
    boolean isCollaborator(@Param("userId") Integer userId, @Param("documentId") Integer documentId);

    @Select("SELECT owner_id FROM document WHERE id = #{documentId}")
    Integer getOwnerId(@Param("documentId") Integer documentId);

    @Insert("INSERT INTO user_document (user_id, document_id) VALUES (#{targetUserId}, #{documentId})")
    void addCollaborator(@Param("targetUserId") Integer targetUserId, @Param("documentId") Integer documentId);

    @Select("SELECT id, owner_id, create_time, last_modified_time, title " +
            "FROM document WHERE id = #{docId}")
    DocumentInfoDto getDocumentInfo(@Param("docId") Integer docId);

    @Select("SELECT title, user.username AS owner_name, create_time " +
            "FROM document INNER JOIN user ON document.owner_id = user.id " +
            "WHERE document.id = #{docId}")
    FileInfoDto getFileInfo(@Param("docId") Integer docId);

    @Delete("DELETE FROM document WHERE id = #{documentId}")
    void deleteDocument(@Param("documentId") Integer documentId);

    @Insert("INSERT INTO document (owner_id, title, code, create_time, last_modified_time) " +
            "VALUES (#{ownerId}, #{title}, #{code}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    void createDocument(Document document);

    @Update("UPDATE document SET code = #{content}, last_modified_time = NOW() WHERE id = #{documentId}")
    void updateContent(@Param("documentId") Integer documentId, @Param("content") String content);

    @Select("SELECT d.id, d.owner_id, u.username AS owner_name, d.create_time, d.last_modified_time, d.title " +
            "FROM document d " +
            "LEFT JOIN user u ON d.owner_id = u.id " +
            "LEFT JOIN user_document ud ON d.id = ud.document_id AND ud.user_id = #{userId} " +
            "WHERE d.owner_id = #{userId} OR ud.user_id = #{userId}")
    List<DocumentInfoDto> getDocumentInfoListByUserId(@Param("userId") Integer userId);

    @Select("SELECT COUNT(1) FROM document WHERE id = #{documentId}")
    boolean existsDocument(@Param("documentId") Integer documentId);
}
