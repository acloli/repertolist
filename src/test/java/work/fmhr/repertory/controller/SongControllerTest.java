package work.fmhr.repertory.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import work.fmhr.repertory.config.CorsConfig;
import work.fmhr.repertory.dto.SongRequest;
import work.fmhr.repertory.dto.SongResponse;
import work.fmhr.repertory.dto.SongSummaryResponse;
import work.fmhr.repertory.exception.GlobalExceptionHandler;
import work.fmhr.repertory.exception.SongNotFoundException;
import work.fmhr.repertory.service.SongService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({ SongController.class, CorsConfig.class, GlobalExceptionHandler.class })
class SongControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private SongService songService;

        @Test
        @DisplayName("GET /api/v1/songs で曲一覧が返る（200 OK）")
        void testList() throws Exception {
                SongResponse response = new SongResponse(
                                1L, "アイドル", "YOASOBI", "+2", "ready", "メモ", LocalDateTime.now());
                when(songService.findAll(null)).thenReturn(List.of(response));

                mockMvc.perform(get("/api/v1/songs"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].id").value(1L))
                                .andExpect(jsonPath("$[0].title").value("アイドル"))
                                .andExpect(jsonPath("$[0].artist").value("YOASOBI"));
        }

        @Test
        @DisplayName("GET /api/v1/songs?status=ready でステータス絞り込み一覧が返る（200 OK）")
        void testListWithStatus() throws Exception {
                SongResponse response = new SongResponse(
                                1L, "アイドル", "YOASOBI", "+2", "ready", "メモ", LocalDateTime.now());
                when(songService.findAll("ready")).thenReturn(List.of(response));

                mockMvc.perform(get("/api/v1/songs").param("status", "ready"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].status").value("ready"));
        }

        @Test
        @DisplayName("GET /api/v1/songs/summary で集計情報が返る（200 OK）")
        void testSummary() throws Exception {
                SongSummaryResponse summaryResponse = new SongSummaryResponse(10L, 5L, 3L, 2L);
                when(songService.getSummary()).thenReturn(summaryResponse);

                mockMvc.perform(get("/api/v1/songs/summary"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.totalCount").value(10L))
                                .andExpect(jsonPath("$.readyCount").value(5L))
                                .andExpect(jsonPath("$.practicingCount").value(3L))
                                .andExpect(jsonPath("$.pendingCount").value(2L));
        }

        @Test
        @DisplayName("GET /api/v1/songs/{id} で曲詳細が返る（200 OK）")
        void testDetail() throws Exception {
                SongResponse response = new SongResponse(
                                1L, "アイドル", "YOASOBI", "+2", "ready", "メモ", LocalDateTime.now());
                when(songService.findById(1L)).thenReturn(response);

                mockMvc.perform(get("/api/v1/songs/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("アイドル"));
        }

        @Test
        @DisplayName("GET /api/v1/songs/{id} で曲が存在しない場合は404 Not Foundが返る")
        void testDetailNotFound() throws Exception {
                when(songService.findById(999L)).thenThrow(new SongNotFoundException(999L));

                mockMvc.perform(get("/api/v1/songs/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value("指定された曲が見つかりません (id=999)"));
        }

        @Test
        @DisplayName("POST /api/v1/songs で曲の新規登録ができる（201 Created）")
        void testCreateSuccess() throws Exception {
                SongResponse created = new SongResponse(
                                1L, "新時代", "Ado", "原曲", "ready", "メモ", LocalDateTime.now());
                when(songService.create(any(SongRequest.class))).thenReturn(created);

                mockMvc.perform(post("/api/v1/songs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "title": "新時代",
                                                    "artist": "Ado",
                                                    "musicKey": "原曲",
                                                    "status": "ready",
                                                    "memo": "メモ"
                                                }
                                                """))
                                .andExpect(status().isCreated())
                                .andExpect(header().string("Location", "/api/v1/songs/1"))
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("新時代"));
        }

        @Test
        @DisplayName("POST /api/v1/songs で曲タイトルが空の場合はバリデーションエラー（400 Bad Request）")
        void testCreateValidationError() throws Exception {
                mockMvc.perform(post("/api/v1/songs")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "title": "",
                                                    "artist": "Ado"
                                                }
                                                """))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.message").value("入力内容に誤りがあります"))
                                .andExpect(jsonPath("$.details").isArray())
                                .andExpect(jsonPath("$.details[0]").value("title: 曲タイトルは必須です"));
        }

        @Test
        @DisplayName("PUT /api/v1/songs/{id} で曲の更新ができる（200 OK）")
        void testUpdateSuccess() throws Exception {
                SongResponse updated = new SongResponse(
                                1L, "新時代 (Remix)", "Ado", "原曲", "ready", "更新メモ", LocalDateTime.now());
                when(songService.update(eq(1L), any(SongRequest.class))).thenReturn(updated);

                mockMvc.perform(put("/api/v1/songs/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                                {
                                                    "title": "新時代 (Remix)",
                                                    "artist": "Ado",
                                                    "musicKey": "原曲",
                                                    "status": "ready",
                                                    "memo": "更新メモ"
                                                }
                                                """))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1L))
                                .andExpect(jsonPath("$.title").value("新時代 (Remix)"));
        }

        @Test
        @DisplayName("DELETE /api/v1/songs/{id} で曲の削除ができる（204 No Content）")
        void testDeleteSuccess() throws Exception {
                doNothing().when(songService).delete(1L);

                mockMvc.perform(delete("/api/v1/songs/1"))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("DELETE /api/v1/songs/{id} で存在しないIDの場合は404 Not Found")
        void testDeleteNotFound() throws Exception {
                doThrow(new SongNotFoundException(999L)).when(songService).delete(999L);

                mockMvc.perform(delete("/api/v1/songs/999"))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.message").value("指定された曲が見つかりません (id=999)"));
        }

        @Test
        @DisplayName("GET /api/v1/songs で予期しない例外が発生した場合は500 Internal Server Errorが返る")
        void testUnexpectedError() throws Exception {
                when(songService.findAll(null)).thenThrow(new RuntimeException("DB接続エラー"));

                mockMvc.perform(get("/api/v1/songs"))
                                .andExpect(status().isInternalServerError())
                                .andExpect(jsonPath("$.status").value(500))
                                .andExpect(jsonPath("$.message").value("サーバー内部でエラーが発生しました"));
        }

        @Test
        @DisplayName("OPTIONS /api/v1/songs でCORSプリフライトが成功し許可ヘッダーが返る")
        void testCorsPreflight() throws Exception {
                mockMvc.perform(options("/api/v1/songs")
                                .header("Origin", "http://localhost:3000")
                                .header("Access-Control-Request-Method", "POST"))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
        }
}
