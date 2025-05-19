package br.edu.utfpr.pb.pw45s.server.ai;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("music")
public class MusicController {
    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping()
    public ResponseEntity<String> generateMusic(){
        return ResponseEntity.ok(musicService.getMusic());
    }

    // /music/parameters?theme={{theme}}&genre={{genre}}
    @GetMapping("/parameters")
    public ResponseEntity<MusicDTO> generateMusicWithParams(@RequestParam("genre") String genre, @RequestParam("theme") String theme) {
        return ResponseEntity.ok(musicService.getMusicByGenreAndTheme(genre, theme));
    }
}