package chess.controller;

import chess.domain.piece.Piece;
import chess.domain.position.Position;
import chess.dto.PiecesDto;
import chess.dto.PlayerDto;
import chess.dto.ScoreDto;
import chess.dto.request.MoveRequestDto;
import chess.dto.request.TurnChangeRequestDto;
import chess.dto.response.MoveResponseDto;
import chess.service.ChessService;
import com.google.gson.Gson;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;
import java.util.Map;

@Controller
public class SpringChessController {
    public static final Gson GSON = new Gson();

    private final ChessService chessService;

    public SpringChessController(final ChessService chessService) {
        this.chessService = chessService;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/start")
    public String start() throws SQLException {
        chessService.remove();
        chessService.makeRound();
        return makeNewGame();
    }

    @GetMapping("/reset")
    public String reset() throws SQLException {
        chessService.remove();
        chessService.resetRound();
        return makeNewGame();
    }

    @GetMapping("/chess")
    public String chess(final Model model) throws SQLException {
        Map<String, String> chessBoardFromDB = chessService.chessBoardFromDB();
        Map<Position, Piece> chessBoard = chessService.chessBoard(chessBoardFromDB);
        Map<String, String> stringChessBoard = chessService.stringChessBoard(chessBoard);
        PiecesDto piecesDto = chessService.piecesDto(chessBoard);

        String jsonFormatChessBoard = GSON.toJson(stringChessBoard);
        model.addAttribute("jsonFormatChessBoard", jsonFormatChessBoard);

        chessService.updateRound(piecesDto);

        String currentTurn = chessService.currentTurn();
        model.addAttribute("currentTurn", currentTurn);

        chessService.changeRoundState(currentTurn);

        PlayerDto playerDto = chessService.playerDto();
        ScoreDto scoreDto = chessService.scoreDto(playerDto);
        chessService.changeRoundToEnd(playerDto);

        model.addAttribute("whiteScore", scoreDto.getWhiteScore());
        model.addAttribute("blackScore", scoreDto.getBlackScore());
        return "chess";
    }

    @PostMapping(value = "/move", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public MoveResponseDto move(@RequestBody MoveRequestDto moveRequestDto) throws SQLException {
        return chessService.move(moveRequestDto);
    }

    @PostMapping(value = "/turn", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void turn(@RequestBody TurnChangeRequestDto turnChangeRequestDto) throws SQLException {
        chessService.changeTurn(turnChangeRequestDto);
    }

    private String makeNewGame() throws SQLException {
        Map<Position, Piece> chessBoard = chessService.chessBoard();
        Map<String, String> filteredChessBoard = chessService.filteredChessBoard(chessBoard);
        chessService.initialize(filteredChessBoard);
        return "redirect:/chess";
    }
}
