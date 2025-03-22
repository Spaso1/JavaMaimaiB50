package org.ast.b50.controller;


import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpSession;
import okhttp3.OkHttpClient;
import org.ast.b50.been.faker.*;
import org.ast.b50.been.lx.Difficulty;
import org.ast.b50.been.lx.LxSongList;
import org.ast.b50.been.lx.Song;
import org.ast.b50.service.MaimaiImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Component
@RestController
@RequestMapping("/api/qq")
public class ApiB50 {
    @Autowired
    MaimaiImageService maimaiImageService;
    private Map<Integer, Song> songs = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            String url = "https://maimai.lxns.net/api/v0/maimai/song/list";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                LxSongList lxSongList = new Gson().fromJson(responseBody, LxSongList.class);
                for (Song song : lxSongList.getSongs()) {
                    if(!(song.getDifficulties().get("dx").length==0)) {
                        //复制一个新的song
                        Song song1 = new Song();
                        song1.setId(song.getId());
                        song1.setTitle(song.getTitle());
                        song1.setArtist(song.getArtist());
                        song1.setGenre(song.getGenre());
                        song1.setBpm(song.getBpm());
                        song1.setVersion(song.getVersion());
                        song1.setRights(song.getRights());
                        Map<String, Difficulty[]> standard = new HashMap<>();
                        Difficulty [] standard1 = song.getDifficulties().get("standard");
                        standard.put("standard", standard1);
                        song1.setDifficulties(standard);
                        song.getDifficulties().remove("standard");

                        songs.put(song.getId(), song1);
                        songs.put(song.getId() + 10000, song);

                    }else {
                        songs.put(song.getId(), song);
                    }
                }
            }
            File file = new File("songs.data");
            try {
                PrintWriter writer = new PrintWriter(file);
                for (Song song : songs.values()) {
                    writer.println("'name':'" + song.getTitle() + "';'id':'" + song.getId() + "'");
                }
                writer.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }catch (Exception e) {}
    }
    @Scheduled(fixedRate = 600000) // 每隔 60 秒执行一次
    public void remove() {
        String folderPath = "/www/wwwroot/cdn.***.cn/resource/b50";
        try {
            Files.walk(Paths.get(folderPath))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {

        }
    }
    @GetMapping("/b50photo")
    public ResponseEntity<byte[]> b50photo(@RequestParam String qq) {
        try {
            String res = "数据.你需要自己传入b50数据";
            //判断文件是否存在
            UserScore userScore = new Gson().fromJson(res, UserScore.class);
            //System.out.println(res);
            List<MusicRating> musicRatings = userScore.getUserRating().getRatingList();
            Map<String,MusicRating> map = new HashMap<>();
            for (MusicRating musicRating : musicRatings) {
                musicRating.setRating(getRatingChartByMusicId(musicRating));
                map.put(musicRating.getMusicId() + "" + musicRating.getLevel(),musicRating);
            }
            musicRatings.addAll(userScore.getUserRating().getNewRatingList());
            for (MusicRating musicRating : musicRatings) {
                musicRating.setRating(getRatingChartByMusicId(musicRating));
                map.put(musicRating.getMusicId() + "" + musicRating.getLevel(),musicRating);
            }
            UserData userData = new UserData();  // 自己提供UserData的内容 , b50页面的rating,名称由这个决定
            ByteArrayInputStream imageStream = maimaiImageService.createComplexImage(userScore, userData);
            BufferedImage image = ImageIO.read(imageStream);

            // 将BufferedImage转换为字节数组
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            headers.setContentDispositionFormData("attachment", "b50photo.png");

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/b50")
    public String b50(@RequestParam String qq) {
        try {
            String res = "数据.你需要自己传入b50数据,比如用api获取水鱼或者落雪的数据";
            UserScore userScore = new Gson().fromJson(res, UserScore.class);
            //System.out.println(res);
            List<MusicRating> musicRatings = userScore.getUserRating().getRatingList();
            Map<String,MusicRating> map = new HashMap<>();
            for (MusicRating musicRating : musicRatings) {
                musicRating.setRating(getRatingChartByMusicId(musicRating));
                map.put(musicRating.getMusicId() + "" + musicRating.getLevel(),musicRating);
            }
            musicRatings.addAll(userScore.getUserRating().getNewRatingList());
            for (MusicRating musicRating : musicRatings) {
                musicRating.setRating(getRatingChartByMusicId(musicRating));
                map.put(musicRating.getMusicId() + "" + musicRating.getLevel(),musicRating);
            }

            UserData userData = new UserData();  // 自己提供UserData的内容 , b50页面的rating,名称由这个决定
            ByteArrayInputStream imageStream = maimaiImageService.createComplexImage(userScore, userData);
            BufferedImage image = ImageIO.read(imageStream);

            // 生成 UUID 作为文件名
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + ".png";
            String folderPath = "/www/wwwroot/cdn.***.cn/resource/b50"; // 替换为你的文件夹路径
            //String folderPath = "/b50"; // 替换为你的文件夹路径

            Path filePath = Paths.get(folderPath, fileName);

            // 确保文件夹存在
            Files.createDirectories(filePath.getParent());

            // 压缩并保存图片到指定文件夹
            compressAndSaveImage(image, filePath);

            // 返回 UUID
            return uuid;
        } catch (IOException e) {
            e.printStackTrace();
            return "保存图片时出错";
        } catch (InterruptedException e) {
            return "失败!代码1,请检查是否绑定";
        } catch (ExecutionException e) {
            return "失败!代码2,请检查是否绑定";
        } catch (FontFormatException e) {
            throw new RuntimeException(e);
        }
    }
    private void compressAndSaveImage(BufferedImage image, Path filePath) throws IOException {
        // 创建一个新的 BufferedImage，移除透明度
        BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = rgbImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // 获取 JPEG 编码器
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(filePath.toFile());
        writer.setOutput(ios);

        // 设置压缩参数
        JPEGImageWriteParam param = new JPEGImageWriteParam(null);
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(0.6f); // 设置压缩质量，0.0f 到 1.0f 之间

        // 写入图片
        writer.write(null, new IIOImage(rgbImage, null, null), param);
        System.out.println("图片压缩完成");
        // 关闭资源
        writer.dispose();
        ios.close();
    }

    public int getRatingChartByMusicId(MusicRating musicRating) {
        int musicId = musicRating.getMusicId();
        double b1 = (double) musicRating.getAchievement() / 10000;
        int dif = musicRating.getLevel();
        if (songs.isEmpty()) {
            String url = "https://maimai.lxns.net/api/v0/maimai/song/list";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                LxSongList lxSongList = new Gson().fromJson(responseBody, LxSongList.class);
                for (Song song : lxSongList.getSongs()) {
                    if(!(song.getDifficulties().get("dx").length==0)) {
                        //复制一个新的song
                        Song song1 = new Song();
                        song1.setId(song.getId());
                        song1.setTitle(song.getTitle());
                        song1.setArtist(song.getArtist());
                        song1.setGenre(song.getGenre());
                        song1.setBpm(song.getBpm());
                        song1.setVersion(song.getVersion());
                        song1.setRights(song.getRights());
                        Map<String, Difficulty[]> standard = new HashMap<>();
                        Difficulty [] standard1 = song.getDifficulties().get("standard");
                        standard.put("standard", standard1);
                        song1.setDifficulties(standard);
                        song.getDifficulties().remove("standard");

                        songs.put(song.getId(), song1);
                        songs.put(song.getId() + 10000, song);

                    }else {
                        songs.put(song.getId(), song);
                    }
                }
            }
            System.out.println(songs.size());
            System.out.println("初始化完成！");
        }
        Song song = null;

        try {
            song = songs.get(musicId);
        }catch(Exception e) {
            e.printStackTrace();
        }
        try {
            if (song.getDifficulties().get("standard").length == 0) {
                musicRating.setMusicName(song.getTitle());
                musicRating.setLevel_info(song.getDifficulties().get("dx")[dif].getLevel_value());
                musicRating.setType("dx");
                return getRatingChart(song.getDifficulties().get("dx")[dif].getLevel_value(), b1);
            } else {
                musicRating.setType("standard");
                musicRating.setMusicName(song.getTitle());
                musicRating.setLevel_info(song.getDifficulties().get("standard")[dif].getLevel_value());
                return getRatingChart(song.getDifficulties().get("standard")[dif].getLevel_value(), b1);
            }
        }catch (Exception e) {
            try {
                musicRating.setMusicName(song.getTitle());
                if (dif < 6) {
                    musicRating.setLevel_info(song.getDifficulties().get("dx")[dif].getLevel_value());
                    musicRating.setType("dx");
                    return getRatingChart(song.getDifficulties().get("dx")[dif].getLevel_value(), b1);
                }
                return 0;
            }catch (Exception a4e) {
                return 0;
            }

        }
    }

    public int getRatingChart(double a1, double b1) {
        double sys = 22.4;
        if (b1 >= 100.5000) {
            return (int) (a1 * 22.512);
        }
        if (b1 == 100.4999) {
            sys = 22.2;
        } else if (b1 >= 100.0000) {
            sys = 21.6;
        } else if (b1 == 99.9999) {
            sys = 21.4;
        } else if (b1 >= 99.5000) {
            sys = 21.1;
        } else if (b1 >= 99.0000) {
            sys = 20.8;
        } else if (b1 >= 98.0000) {
            sys = 20.3;
        } else if (b1 >= 97.0000) {
            sys = 20.0;
        } else {
            sys = 0;
        }
        return (int) (a1 * sys * b1 / 100);
    }
}
