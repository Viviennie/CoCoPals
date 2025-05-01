package com.tongji.codejourneycolab.codejourneycolabbackend.controller;

import com.tongji.codejourneycolab.codejourneycolabbackend.dto.DocumentContentDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.DocumentInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.FileInfoDto;
import com.tongji.codejourneycolab.codejourneycolabbackend.dto.InvitationCodeDTO;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.DocInvitationCodeException;
import com.tongji.codejourneycolab.codejourneycolabbackend.exception.DocPermissionException;
import com.tongji.codejourneycolab.codejourneycolabbackend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @GetMapping("/getfilelist")
    public ResponseEntity<List<DocumentInfoDto>> getFileList(@RequestAttribute Integer id) {
        return ResponseEntity.ok(documentService.getDocumentInfoList(id)); /// TODO: handle exception
    }

    @GetMapping("/getfileinfo")
    public ResponseEntity<FileInfoDto> getFileListById(@RequestAttribute Integer id, @RequestParam Integer documentId) {
        return ResponseEntity.ok(documentService.getFileInfo(id, documentId));
    }

    @GetMapping("/getcontent")
    public ResponseEntity<String> getContent(@RequestAttribute Integer id,
                                             @RequestParam Integer documentId) {
        return ResponseEntity.ok(documentService.getContent(id, documentId));
    }

    @PostMapping("/savecontent")
    public ResponseEntity<String> saveContent(@RequestAttribute Integer id, @RequestBody DocumentContentDto documentContentDto) {
        try {
            // docCode 为打开文档所需的共享码，code为文档代码内容
            documentService.updateContent(id, documentContentDto.getDocumentCode(), documentContentDto.getCode());
            return ResponseEntity.ok("success");
        } catch (DocInvitationCodeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (DocPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createDocument(@RequestAttribute Integer id,
                                                              @RequestParam String title) {
        return ResponseEntity.ok(documentService.createDocument(id, title).getId().toString());
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteDocument(@RequestAttribute Integer id,
                                                              @RequestParam Integer documentId) {
        try {
            documentService.deleteDocument(id, documentId);
            return ResponseEntity.ok("success");
        } catch (DocPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/generatecode")
    public ResponseEntity<String> generateCode(@RequestAttribute Integer id,
                                                            @RequestParam Integer documentId) {
        try {
            String invitationCode = documentService.getDocumentShareCode(id, documentId);
            return ResponseEntity.ok(invitationCode);
        } catch (DocInvitationCodeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (DocPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/connectServiceByInvitation")
    public ResponseEntity<String> connectServiceByInvitation(@RequestAttribute Integer id, @RequestBody InvitationCodeDTO invitation) {
        try {
            Integer documentId =  documentService.joinCollaborationByCode(id, invitation.getInvitationCode());
            return ResponseEntity.ok(documentId.toString());
        } catch (DocInvitationCodeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (DocPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/connectServiceById")
    public ResponseEntity<String> connectServiceById(@RequestAttribute Integer id, @RequestParam Integer documentId) {
        try {
            return ResponseEntity.ok(documentService.joinCollaborationById(id, documentId));
        } catch (DocInvitationCodeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }catch (DocPermissionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
