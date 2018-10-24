package club.goldri.web.system.controller;

import club.goldri.core.util.ResponseUtil;
import club.goldri.core.websocket.MsgWebsocket;
import club.goldri.core.util.AipSpeechUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.BinaryMessage;


@RestController
@RequestMapping("/api")
public class AipSpeechController {
    @Autowired
    private MsgWebsocket speechWebsocketEndPoint;

    @GetMapping("/speech")
    public ResponseEntity aipSpeech(){
        return ResponseUtil.success(AipSpeechUtil.synthesis("消息"));
    }

    @GetMapping("/websocket")
    public ResponseEntity websocket(){
        BinaryMessage message = new BinaryMessage(AipSpeechUtil.synthesis("后台发来的消息"));
//        speechWebsocketEndPoint.sendMsgToAllClient(message);
        return ResponseUtil.success();
    }
}
