package org.ast.b50.service;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.ast.b50.been.faker.*;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.*;import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.AttributedString;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

@Service
public class MaimaiImageService {
    private OkHttpClient client = new OkHttpClient();
    private boolean useLocalImageForTesting = false; // 设置为true以使用本地图像进行测试

    public ByteArrayInputStream createComplexImage(UserScore userScore, UserData userData) throws IOException, ExecutionException, InterruptedException, FontFormatException {
        // 加载主底图和卡片
        BufferedImage backgroundImage = ImageIO.read(Paths.get(".//res//b50_bg.png").toFile());
        BufferedImage cardImage = ImageIO.read(Paths.get(".//res//b50_score_master.png").toFile());

        // 创建最终图像的画布
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();
        BufferedImage finalImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = finalImage.createGraphics();
        // 绘制主底图
        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.drawImage(backgroundImage, 0, 0, null);

        // 计算卡片放置位置
        int totalCards = 50;
        int cardsPerRow = 5;
        int cardSpacingX = 20;
        int cardSpacingY = 10;
        int cardWidth = cardImage.getWidth();
        int cardHeight = cardImage.getHeight();

        // 计算总宽度和起始位置
        int totalWidth = (cardWidth * cardsPerRow) + (cardSpacingX * (cardsPerRow - 1));
        int startX = (width - totalWidth) / 2; // 居中对称

        BufferedImage userBackgroundImage = getImageAndResize(".//res//UI_Plate_300501.png",200);
        String shuiyin = "FindMaimai Bot - Powered By ReisaSpasol ";
        {
            // 添加文本
            Font font = new Font("Arial", Font.BOLD, 48);
            g2d.setFont(font);
            g2d.setColor(Color.WHITE);
            g2d.drawString(shuiyin, 610, 2480);
        }


        int rating = userData.getPlayerRating();
        int r1 ,r2,r3,r4,r5=0;
        if (rating >= 10000) {
            r1 = 1;
            r2 = (rating - 10000) / 1000;
            r3 = (rating - 10000 - r2 * 1000) / 100;
            r4 = (rating - 10000 - r2 * 1000 - r3 * 100) / 10;
            r5 = rating - 10000 - r2 * 1000 - r3 * 100 - r4 * 10;
        }else  {
            r1 = 0;
            r2 = rating / 1000;
            r3 = (rating - (r2 * 1000)) / 100;
            r4 = (rating - (r2 * 1000) - r3 * 100) / 10;
            r5 = rating - (r2 * 1000) - r3 * 100 - r4 * 10;
        }
        int iconId= userData.getIconId();
        String username = userData.getUserName();
        int b35rating = 0;
        int b15rating = 0;

        List<MusicRating> b50 = userScore.getUserRating().getRatingList();
        List<MusicRating> b15 = userScore.getUserRating().getNewRatingList();
        BufferedImage ratingImage = null;
        if (rating >= 15000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_11.png",60);
        }else if (rating >= 14500) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_10.png",60);
        } else if (rating >= 14000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_9.png",60);
        } else if (rating >= 13000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_8.png",60);
        } else if (rating>= 12000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_7.png",60);
        } else if (rating >= 10000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_6.png",60);
        }
        // 计算底部图片的位置
        // int bottomImageY = cardY + cardHeight -17; // 文本位于卡片下方15像素处
        // 绘制底部图片
        g2d.drawImage(userBackgroundImage, startX, 20, null);
        BufferedImage iconImage = getImage("https://assets2.lxns.net/maimai/icon/" + iconId +".png",true,180);
        g2d.drawImage(iconImage, startX + 15, 30, null);

        BufferedImage nameImage = getImage(".//res//Name.png");
        g2d.drawImage(nameImage, startX + 200, 90, null);
        {
            // 加载自定义字体文件（假设字体文件路径为 "path/to/your/font.ttf"）
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                    .deriveFont(Font.PLAIN, 44); // 设置字体样式和大小

            g2d.setFont(customFont); // 设置 Graphics2D 使用的字体
            g2d.setColor(Color.BLACK);
            int textX = startX + 200 + 30;
            int textY = 145;
            g2d.drawString(username, textX, textY);
        }

        BufferedImage versionImage = getImageAndResize(".//res//maimai でらっくす BUDDiES.png",250);
        g2d.drawImage(versionImage, 1600, 0, null);
        g2d.drawImage(ratingImage, startX + 200, 26, null);

        if(r1==1) {
            BufferedImage r1Image = getImageAndResize(".//res//UI_NUM_Drating_1.png",35);
            g2d.drawImage(r1Image, startX + 200 + 136, 39, null);
        }
        if (rating >= 1000) {
            BufferedImage r2Image = getImageAndResize(".//res//UI_NUM_Drating_" + r2 + ".png",35);
            g2d.drawImage(r2Image, startX + 200 + 162 , 39, null);
        }
        if (rating >= 100) {
            BufferedImage r3Image = getImageAndResize(".//res//UI_NUM_Drating_" + r3 + ".png",35);
            g2d.drawImage(r3Image, startX + 200 + 187, 39, null);
        }
        if (rating >= 10) {
            BufferedImage r4Image = getImageAndResize(".//res//UI_NUM_Drating_" + r4 + ".png",35);
            g2d.drawImage(r4Image, startX + 200 + 212, 39, null);
        }
        if (rating >= 1) {
            BufferedImage r5Image = getImageAndResize(".//res//UI_NUM_Drating_" + r5 + ".png",35);
            g2d.drawImage(r5Image, startX + 200 + 237, 39, null);
        }
// 创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10); // 根据需要调整线程池大小

        List<Future<BufferedImage>> futures = new ArrayList<>();


        // 计算第一行的起始Y位置
        int firstRowY = (int) (0.1 * height); // 第一行距离上方0.1个底图高度的距离

        // 计算行间距
        int rowSpacing = cardHeight + cardSpacingY;
        int additionalSpacing = (int) (0.05 * height); // 第八行距离第七行0.1个底图高度的距离
        // 提交所有任务到线程池
        for (int i = 0; i < totalCards; i++) {
            int id = b50.get(i).getMusicId();
            Callable<BufferedImage> task = () -> getAndResizeOverlayImage(145, 145, id);
            futures.add(executorService.submit(task));
        }

        for (int i = 0; i < totalCards; i++) {
            System.out.println(i + b50.get(i).toString());
            int row = i / cardsPerRow;
            int col = i % cardsPerRow;

            int cardX = startX + (col * (cardWidth + cardSpacingX));
            int cardY = firstRowY + (row * rowSpacing);

            // 如果是第八行，增加额外的间距
            if (row >= 7) {
                cardY += additionalSpacing;
                b15rating += b50.get(i).getRating();
            }else{
                b35rating += b50.get(i).getRating();
            }
            int difference = b50.get(i).getLevel();
            String cardImagePath = ".//res//b50_score_master.png"; // 默认背景图像路径

            switch (difference) {
                case 1:
                    cardImagePath = ".//res//b50_score_advanced.png";
                    break;
                case 2:
                    cardImagePath = ".//res//b50_score_expert.png";
                    break;
                case 3:
                    cardImagePath = ".//res//b50_score_master.png";
                    break;
                case 4:
                    cardImagePath = ".//res//b50_score_remaster.png";
                    break;
                case 0:
                    cardImagePath = ".//res//b50_score_basic.png";
                    break;
                // 添加更多 case 根据需要
                default:
                    cardImagePath = ".//res//b50_score_master.png"; // 默认背景图像路径
                    break;
            }
            BufferedImage cardImage1 = ImageIO.read(new File(cardImagePath));

            // 绘制卡片
            g2d.drawImage(cardImage1, cardX, cardY, null);

            // 获取并缩放叠加图像
            BufferedImage overlayImage = futures.get(i).get();

            // 计算叠加图像的位置
            int overlayX = cardX + 6;
            int overlayY = cardY + (cardHeight - (int) (overlayImage.getHeight() * 0.9)) / 2 - 6;

            // 绘制叠加图像
            g2d.drawImage(overlayImage, overlayX, overlayY, (int) (overlayImage.getWidth() * 0.9), (int) (overlayImage.getHeight() * 0.9), null);
            String music_id = b50.get(i).getMusicId() + "";
            {
                // 添加文本
                Font font = new Font("Arial", Font.BOLD, 18);
                g2d.setFont(font);
                g2d.setColor(Color.WHITE);
                int textX = cardX + 6+ 13;
                int textY = cardY + cardHeight -4; // 文本位于卡片下方15像素处
                if(difference == 4) {
                    //颜色使用黑色
                    g2d.setColor(Color.BLACK);
                }
                g2d.drawString(music_id, textX, textY);
            }
            String music_name = b50.get(i).getMusicName();
            drawTruncatedText(g2d, music_name, cardX + 155, cardY + 27, 240,difference);
            int r = b50.get(i).getAchievement();
            double c = (double) r / 10000;
            String achievement = c + "%";
            {
                // 添加文本
                Font font = new Font("Arial", Font.BOLD, 32);
                g2d.setFont(font);
                g2d.setColor(Color.WHITE);
                if(difference == 4) {
                    //颜色使用黑色
                    g2d.setColor(Color.BLACK);
                }
                int textX = cardX + 155;
                int textY = cardY + 64;
                g2d.drawString(achievement, textX, textY);
            }
            String diff2rate =  b50.get(i).getLevel_info() +" -> " + b50.get(i).getRating() + " PC:" + b50.get(i).getPlayCount();
            {
                // 添加文本
                Font font = new Font("Arial", Font.BOLD, 24);
                g2d.setFont(font);
                g2d.setColor(Color.WHITE);
                if(difference == 4) {
                    //颜色使用黑色
                    g2d.setColor(Color.BLACK);
                }
                int textX = cardX + 155;
                int textY = cardY + 88;
                g2d.drawString(diff2rate, textX, textY);
            }

            int comboType = b50.get(i).getComboStatus();
            if(comboType == 1) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_FC.png",55);
                g2d.drawImage(imageView, cardX + 235, cardY + 97, null);
            }else if(comboType == 2) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_FCp.png",55);
                g2d.drawImage(imageView, cardX + 235, cardY + 97, null);
            }else if(comboType == 3) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_AP.png",55);
                g2d.drawImage(imageView, cardX + 235, cardY + 97, null);
            }else if(comboType == 4) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_APp.png",55);
                g2d.drawImage(imageView, cardX + 235 ,cardY + 97, null);
            }

            int syncStatus = b50.get(i).getSyncStatus();
            if(syncStatus == 5) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_Sync.png",55);
                g2d.drawImage(imageView, cardX + 300, cardY + 97, null);
            }if(syncStatus == 1) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_FS.png",55);
                g2d.drawImage(imageView, cardX + 300, cardY + 97, null);
            }
            if(syncStatus == 2) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_FSp.png",55);
                g2d.drawImage(imageView, cardX + 300, cardY + 97, null);
            }
            if(syncStatus == 3) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_FSD.png",55);
                g2d.drawImage(imageView, cardX + 300, cardY + 97, null);
            }
            if(syncStatus == 4) {
                BufferedImage imageView = getImageAndResize(".//res//UI_MSS_MBase_Icon_FSDp.png",55);
                g2d.drawImage(imageView, cardX + 300, cardY + 97, null);
            }


            int feat = 0;

            if(b50.get(i).getAchievement() > 1005000) {
                feat = 0;
            } else if (b50.get(i).getAchievement() > 1000000) {
                feat = 1;
            } else if (b50.get(i).getAchievement() > 995000) {
                feat = 2;
            } else if (b50.get(i).getAchievement() > 990000) {
                feat = 3;
            } else if (b50.get(i).getAchievement() > 980000) {
                feat = 4;
            } else if (b50.get(i).getAchievement() > 970000) {
                feat = 5;
            } else if (b50.get(i).getAchievement() > 940000) {
                feat = 6;
            } else if (b50.get(i).getAchievement() > 900000) {
                feat = 7;
            } else if (b50.get(i).getAchievement() > 800000) {
                feat = 8;
            }

            BufferedImage featImage = null;
            if(feat == 0) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_SSSp.png",40);
            }else if(feat == 1) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_SSS.png",40);
            }else if(feat == 2) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_SSp.png",40);

            }else if(feat == 3) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_SS.png",40);

            }else if(feat == 4) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_Sp.png",40);
            }else if(feat == 5) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_S.png",40);
            }else if (feat == 6) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_AAA.png",40);
            }else if (feat == 7) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_AA.png",40);
            } else if (feat == 8) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_A.png",40);
            } else if (feat == 9) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_BBB.png",40);
            } else if (feat == 10) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_BB.png",40);
            } else if (feat == 11) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_B.png",40);
            } else if (feat == 12) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_C.png",40);
            } else if (feat == 13) {
                featImage = getImageAndResize(".//res//UI_TTR_Rank_D.png",40);
            }
            // 计算底部图片的位置
            int featImageX = cardX + 155;
            int featImageY = cardY + 100; // 文本位于卡片下方15像素处
            // 绘制底部图片
            g2d.drawImage(featImage, featImageX, featImageY, null);

            // 获取并缩放底部图片
            BufferedImage bottomImage = getAndResizeBottomImage(18, b50.get(i).getType());

            // 计算底部图片的位置
            int bottomImageX = cardX + 6+ 76;
            int bottomImageY = cardY + cardHeight -17; // 文本位于卡片下方15像素处
            // 绘制底部图片
            g2d.drawImage(bottomImage, bottomImageX, bottomImageY, null);
        }



        BufferedImage ratingDetailImage = getImageAndResize(".//res//UI_CMN_Shougou_Rainbow.png",60);
        g2d.drawImage(ratingDetailImage, startX + 200, 160, null);
        {
            String ratingDetailText = "B35: " + b35rating + " + B15: " + b15rating + " = " + (b35rating + b15rating);
            Font font = new Font("Arial", Font.BOLD, 24);
            g2d.setFont(font);
            g2d.setColor(Color.BLACK);
            g2d.drawString(ratingDetailText, startX + 230, 196);
        }
        // 释放绘图资源
        g2d.dispose();

        // 将图像转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(finalImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

        return imageStream;
    }
    private void drawTextWithOutline(Graphics2D g2d, String text, Font font, Color textColor, Color outlineColor, int outlineThickness, int x, int y) {
        // 设置文本属性
        AttributedString attributedString = new AttributedString(text);
        Map<TextAttribute, Object> attributes = new HashMap<>();
        attributes.put(TextAttribute.FONT, font);
        attributedString.addAttributes(attributes, 0, text.length());

        // 创建TextLayout
        TextLayout textLayout = new TextLayout(attributedString.getIterator(), g2d.getFontRenderContext());

        // 绘制黑色边框
        for (int i = -outlineThickness; i <= outlineThickness; i++) {
            for (int j = -outlineThickness; j <= outlineThickness; j++) {
                if (i != 0 || j != 0) {
                    g2d.setColor(outlineColor);
                    textLayout.draw(g2d, x + i, y + j);
                }
            }
        }

        // 绘制金色文本
        g2d.setColor(textColor);
        textLayout.draw(g2d, x, y);
    }
    private void drawTruncatedText(Graphics2D g2d, String text, int x, int y, int maxWidth) throws UnsupportedEncodingException, FontFormatException, IOException {
        // 加载自定义字体文件（假设字体文件路径为 "path/to/your/font.ttf"）
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                .deriveFont(Font.PLAIN, 24); // 设置字体样式和大小

        g2d.setFont(customFont); // 设置 Graphics2D 使用的字体
        FontMetrics fm = g2d.getFontMetrics();

        int textWidth = fm.stringWidth(text);

        if (textWidth > maxWidth) {
            int maxLength = text.length();
            while (fm.stringWidth(text.substring(0, maxLength) + "...") > maxWidth) {
                maxLength--;
            }
            text = text.substring(0, maxLength) + "...";
        }

        g2d.drawString(new String(text.getBytes(StandardCharsets.UTF_8)), x, y);
    }

    private void drawTruncatedText(Graphics2D g2d, String text, int x, int y, int maxWidth,int difference) throws IOException, FontFormatException {
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                .deriveFont(Font.PLAIN, 24); // 设置字体样式和大小

        g2d.setFont(customFont); // 设置 Graphics2D 使用的字体
        FontMetrics fm = g2d.getFontMetrics();
        if(difference == 4) {
            //颜色使用黑色
            g2d.setColor(Color.BLACK);
        }
        int textWidth = fm.stringWidth(text);

        if (textWidth > maxWidth) {
            // Calculate the maximum length of the text that fits within maxWidth
            int maxLength = text.length();
            while (fm.stringWidth(text.substring(0, maxLength) + "...") > maxWidth) {
                maxLength--;
            }
            text = text.substring(0, maxLength) + "...";
        }
        g2d.drawString(new String(text.getBytes(StandardCharsets.UTF_8)), x, y);
    }
    // 在createComplexImage方法中调用drawTruncatedText

    private BufferedImage getAndResizeOverlayImage(int targetWidth, int targetHeight,int id) throws IOException {
        BufferedImage originalImage;
        if(id>10000) {
            id = id-10000;
        }
        if (useLocalImageForTesting) {
            // 使用本地图像进行测试
            originalImage = ImageIO.read(new File(".//res//img.png")); // 替换为你的本地图像路径
        } else {
            // 从网络获取图像
            String imageUrl = "https://assets2.lxns.net/maimai/jacket/"+id+".png";
            Request request = new Request.Builder()
                    .url(imageUrl)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                try (InputStream inputStream = response.body().byteStream()) {
                    originalImage = ImageIO.read(inputStream);
                }
            }
        }

        // 缩放图像
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resizedImage;
    }
    private BufferedImage getImage(String path) throws IOException {
        BufferedImage originalImage;
        originalImage = ImageIO.read(new File(path)); // 替换为你的本地图像路径
        return originalImage;
    }
    private BufferedImage getImage(String url,boolean useInternet) throws IOException {
        BufferedImage originalImage;
        String imageUrl = url;
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            try (InputStream inputStream = response.body().byteStream()) {
                originalImage = ImageIO.read(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return originalImage;
    }
    private BufferedImage getImage(String url,boolean useInternet,int targetHeight) throws IOException {
        BufferedImage originalImage;
        String imageUrl = url;
        Request request = new Request.Builder()
                .url(imageUrl)
                .build();
        System.out.println(imageUrl);
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            try (InputStream inputStream = response.body().byteStream()) {
                originalImage = ImageIO.read(inputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int targetWidth = (int) ((double) originalWidth / originalHeight * targetHeight);

        // 缩放图像
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }
    private BufferedImage getImageAndResize(String path, int targetHeight) throws IOException {
        BufferedImage originalImage;
        originalImage = ImageIO.read(new File(path)); // 替换为你的本地图像路径
        // 计算缩放后的宽度
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int targetWidth = (int) ((double) originalWidth / originalHeight * targetHeight);

        // 缩放图像
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return resizedImage;
    }
    private BufferedImage getAndResizeBottomImage(int targetHeight,String type) throws IOException {
        BufferedImage originalImage;
        if(type.equals("standard")) {
            originalImage = ImageIO.read(new File(".//res//SD.png"));
        }else {
            originalImage = ImageIO.read(new File(".//res//DX.png")); // 替换为你的本地图像路径

        }

        // 计算缩放后的宽度
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int targetWidth = (int) ((double) originalWidth / originalHeight * targetHeight);

        // 缩放图像
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    private BufferedImage generateUsernameBackground(String userName) throws IOException {
        // 这里简单模拟生成一个带有用户名的背景图像
        // 实际应用中，你可能需要根据具体需求创建更复杂的图像
        int width = 300;
        int height = 50;
        BufferedImage tempImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = tempImage.createGraphics();

        // 设置字体和颜色
        Font font = new Font("Arial", Font.BOLD, 24);
        g2d.setFont(font);
        g2d.setColor(Color.BLACK);

        // 绘制文本
        g2d.drawString(userName, 10, 30);

        // 释放绘图资源
        g2d.dispose();

        return tempImage;
    }
    public ByteArrayInputStream createCommitPhoto(String data, UserData userData, RegionData regionData) throws IOException, ParseException, FontFormatException {
        // 解析数据
        Map<String, Integer> commitMap = parseCommitData(data);

        // 加载底图
        BufferedImage backgroundImage = ImageIO.read(Paths.get(".//res//UI_TTR_BG_Base.png").toFile());
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();

        int targetX = 50;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 设置底图透明度
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g2d.drawImage(backgroundImage, 0, 0, null);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f)); // 恢复默认透明度

        // 设置字体
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(Color.GRAY); // 暗色文字

        BufferedImage pageup = getImageAndResize(".//res//pageup.png",180);
        g2d.drawImage(pageup, 800, 200, null);
        BufferedImage moon = getImageAndResize(".//res//moon.png",180);
        g2d.drawImage(moon, 800, 20, null);
        BufferedImage sns_bg = getImageAndResize(".//res//sns_bg.png",180);
        g2d.drawImage(sns_bg, 600, 400, null);
        // 从文件获取
        BufferedImage image2 = null;
        int random = new Random().nextInt(2);
        try {
            if(random == 0) {
                image2 = getImageAndResize(".//res//chara-left.png",604);
            }else {
                image2 =getImageAndResize(".//res//chara.png",604);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //绘制在最上方
        g2d.drawImage(image2, -100, 0, null);

        generateName(userData, g2d);
        // 获取当前日期
        Date startDate = new Date();
        int daysInYear = 245; // 简化处理，不考虑闰年

        // 计算星期日期的最大宽度
        FontMetrics fm = g2d.getFontMetrics();
        int maxDayOfWeekWidth = 0;
        for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
            Date dayDate = addDays(startDate, -dayOfWeek);
            String dayStr = new SimpleDateFormat("EEE").format(dayDate);
            int dayWidth = fm.stringWidth(dayStr);
            if (dayWidth > maxDayOfWeekWidth) {
                maxDayOfWeekWidth = dayWidth;
            }
        }

        int min_y = 230;
        int max_y = 140 + 55 + 205;
        int max_x = width - maxDayOfWeekWidth + 25 + targetX; // 在右边边缘
        //画0.4f透明度的白色矩形fillRoundRect
        g2d.setColor(new Color(255, 255, 255, 182));
        g2d.fillRoundRect(200, min_y, max_x, max_y - min_y, 20, 20);

        g2d.fillRoundRect(200, min_y + 190, max_x, max_y - min_y, 20, 20);

        // 绘制星期日期
        for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
            Date dayDate = addDays(startDate, -dayOfWeek);
            String dayStr = new SimpleDateFormat("EEE").format(dayDate);
            int x = width - maxDayOfWeekWidth - 5; // 在右边边缘
            int y = dayOfWeek * 20 + 55 + 250; // 加上上边的padding
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                    .deriveFont(Font.PLAIN, 12); // 设置字体样式和大小
            g2d.setFont(font);
            Color color;
            color = new Color(115, 215, 102);
            g2d.setColor(color);
            g2d.drawString(dayStr, x + targetX, y - 50);
        }

        // 调整每个框的大小和间距
        int boxSize = 15; // 每个框的大小
        int boxSpacing = 5; // 每个框之间的间距

        for (int day = 0; day < daysInYear; day++) {
            Date currentDate = addDays(startDate, -day);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = dateFormat.format(currentDate);

            int week = day / 7;
            int dayOfWeek = day % 7;
            int x = width - (week * (boxSize + boxSpacing) + 10 + maxDayOfWeekWidth + 10 + boxSize) + 5 + targetX; // 从右往左
            int y = dayOfWeek * (boxSize + boxSpacing) + 45 + 200; // 从上往下

            // 获取提交次数
            int commits = commitMap.getOrDefault(dateStr, 0);
            if(!(commits==0)) {
                System.out.println(currentDate);
            }
            // 根据提交次数设置颜色
            Color color;
            if (commits==0) {
                color = new Color(105, 101, 101, 142);
            }else if (commits<=5) {
                color = new Color(0, 255, 0); // 最亮的绿色
            }else if (commits<=10) {
                color = new Color(0, 200, 0); // 更亮的绿色
            }else if (commits<=15) {
                color = new Color(0, 150, 0); // 更亮的绿色
            }else {
                color = new Color(0, 100, 0); // 暗色绿色
            }
            // 绘制圆角矩形
            g2d.setColor(color);
            g2d.fillRoundRect(x, y, boxSize, boxSize, 3, 3);
        }
        //画less more
        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("less", 600, 395);
        Color color = new Color(0, 255, 0); // 最亮的绿色
        g2d.setColor(color);
        g2d.fillRoundRect(600+55,397 -boxSize, boxSize, boxSize, 3, 3);
        color = new Color(0, 200, 0); // 更亮的绿色
        g2d.setColor(color);
        g2d.fillRoundRect(600+55+boxSize + boxSpacing,397-boxSize , boxSize, boxSize, 3, 3);
        color = new Color(0, 150, 0);
        g2d.setColor(color);
        g2d.fillRoundRect(600+55+(boxSize+boxSpacing)*2,397-boxSize , boxSize, boxSize, 3, 3);
        color = new Color(0, 100, 0);
        g2d.setColor(color);
        g2d.fillRoundRect(600+55+(boxSize+boxSpacing)*3, 397-boxSize, boxSize, boxSize, 3, 3);
        g2d.drawString("more", 600+55+(boxSize+boxSpacing)*5, 395);

        int sum = 0;
        int a =0;
        for (UserRegion userRegion : regionData.getUserRegionList()) {
            sum += userRegion.getPlayCount();
            if (userRegion.getPlayCount() > 0) {
                String province = userRegion.getProvince();
                int l = a / 6;
                if (province != null && !province.isEmpty()) {
                    int x = 300 + (l * 150) ;
                    int y =  max_y + 40 + (a * 25) - (l * 150);
                    Font font = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                            .deriveFont(Font.PLAIN, 20); // 设置字体样式和大小
                    g2d.setFont(font);
                    g2d.setColor(new Color(0, 0, 0));
                    g2d.drawString(province + " : " + userRegion.getPlayCount(), x, y);
                }
            }
            a++;
        }
        Font font = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                .deriveFont(Font.PLAIN, 18); // 设置字体样式和大小
        g2d.setFont(font);
        g2d.drawString( "Play Count : " + sum, 300, 588);
        BufferedImage qr = getImageAndResize(".//res//image.jpg",150);
        g2d.drawImage(qr, 880, 430, null);
        // 释放绘图资源
        g2d.dispose();

        // 将图像转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);

        return imageStream;
    }

    private void generateName(UserData userData, Graphics2D g2d) throws IOException, FontFormatException {
        int rating = userData.getPlayerRating();
        int iconId= userData.getIconId();
        String username = userData.getUserName();
        int r1 ,r2,r3,r4,r5=0;
        if (rating >= 10000) {
            r1 = 1;
            r2 = (rating - 10000) / 1000;
            r3 = (rating - 10000 - r2 * 1000) / 100;
            r4 = (rating - 10000 - r2 * 1000 - r3 * 100) / 10;
            r5 = rating - 10000 - r2 * 1000 - r3 * 100 - r4 * 10;
        }else  {
            r1 = 0;
            r2 = rating / 1000;
            r3 = (rating - (r2 * 1000)) / 100;
            r4 = (rating - (r2 * 1000) - r3 * 100) / 10;
            r5 = rating - (r2 * 1000) - r3 * 100 - r4 * 10;
        }
        int startX = 500;

        BufferedImage iconImage = getImage("https://assets2.lxns.net/maimai/icon/" + iconId +".png",true,150);
        g2d.drawImage(iconImage, startX - 100, 30, null);


        BufferedImage nameImage = getImage(".//res//Name.png");
        g2d.drawImage(nameImage, startX + 80, 30, null);
        Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                .deriveFont(Font.PLAIN, 44); // 设置字体样式和大小

        g2d.setFont(customFont); // 设置 Graphics2D 使用的字体
        g2d.setColor(Color.BLACK);
        int textX = startX + 110;
        int textY = 80;
        g2d.drawString(username, textX, textY);



        BufferedImage ratingImage = null;
        if (rating >= 15000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_11.png",60);
        }else if (rating >= 14500) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_10.png",60);
        } else if (rating >= 14000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_9.png",60);
        } else if (rating >= 13000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_8.png",60);
        } else if (rating>= 12000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_7.png",60);
        } else if (rating >= 10000) {
            ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_6.png",60);
        }
        g2d.drawImage(ratingImage, startX + 80, 110, null);
        if(r1==1) {
            BufferedImage r1Image = getImageAndResize(".//res//UI_NUM_Drating_1.png",35);
            g2d.drawImage(r1Image, startX + 82 + 136, 126, null);
        }
        if (rating >= 1000) {
            BufferedImage r2Image = getImageAndResize(".//res//UI_NUM_Drating_" + r2 + ".png",35);
            g2d.drawImage(r2Image, startX + 82 + 162 , 126, null);
        }
        if (rating >= 100) {
            BufferedImage r3Image = getImageAndResize(".//res//UI_NUM_Drating_" + r3 + ".png",35);
            g2d.drawImage(r3Image, startX + 82 + 187, 126, null);
        }
        if (rating >= 10) {
            BufferedImage r4Image = getImageAndResize(".//res//UI_NUM_Drating_" + r4 + ".png",35);
            g2d.drawImage(r4Image, startX + 82 + 212, 126, null);
        }
        if (rating >= 1) {
            BufferedImage r5Image = getImageAndResize(".//res//UI_NUM_Drating_" + r5 + ".png",35);
            g2d.drawImage(r5Image, startX + 82 + 237, 126, null);
        }
    }


    private Map<String, Integer> parseCommitData(String data) throws ParseException {
        Map<String, Integer> commitMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String[] entries = data.split(",");
        for (String entry : entries) {
            String[] parts = entry.split("@");
            int commits = Integer.parseInt(parts[1]);
            commitMap.put(parts[0], commits);
        }

        return commitMap;
    }

    private Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    public ByteArrayInputStream createRankList(List<UserData> userDatas) throws IOException, ParseException, FontFormatException {
        int row = userDatas.size()/8;
        //每一列7行
        int width = 856 * (row + 1);
        int height = (566 + 10) * userDatas.size() ;
        if ( userDatas.size() > 7) {
            System.out.println("用户数大于7");
            height = (566 + 10) * 7;
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // 创建 Graphics2D 对象,并且读取图片 person_bg.png 按照userdatas的元素从上到下覆盖图片
        Graphics2D g2d = image.createGraphics();
        for (int i = 0; i < userDatas.size(); i++) {
            int row2 = i/7;
            UserData userData = userDatas.get(i);
            BufferedImage person_bg = getImageAndResize(".//res//person_bg.png",566);
            g2d.drawImage(person_bg,  856 * row2, (566 + 10)* (i - row2*7) , null);
            //填充用户名,Rating和用户图像,重写
            int rating = userData.getPlayerRating();
            int iconId= userData.getIconId();
            String username = userData.getUserName();
            int r1 ,r2,r3,r4,r5=0;
            if (rating >= 10000) {
                r1 = 1;
                r2 = (rating - 10000) / 1000;
                r3 = (rating - 10000 - r2 * 1000) / 100;
                r4 = (rating - 10000 - r2 * 1000 - r3 * 100) / 10;
                r5 = rating - 10000 - r2 * 1000 - r3 * 100 - r4 * 10;
            }else  {
                r1 = 0;
                r2 = rating / 1000;
                r3 = (rating - (r2 * 1000)) / 100;
                r4 = (rating - (r2 * 1000) - r3 * 100) / 10;
                r5 = rating - (r2 * 1000) - r3 * 100 - r4 * 10;
            }
            int startX = 300 +856 * row2;

            int ra = new Random().nextInt(4);
            if (ra == 1) {
                BufferedImage paimage = getImageAndResize(".//res//chara.png",500);
                g2d.drawImage(paimage, 120 + 856 * row2, (566 + 10)* (i - row2*7) + 120   , null);
            }else if (ra == 3) {
                BufferedImage paimage = getImageAndResize(".//res//chara-right.png",500);
                g2d.drawImage(paimage, 120 + 856 * row2, (566 + 10)* (i - row2*7) + 120   , null);
            }else if (ra == 2) {
                BufferedImage paimage = getImageAndResize(".//res//chara-left.png",500);
                g2d.drawImage(paimage, 120 + 856 * row2, (566 + 10)* (i - row2*7) + 120   , null);
            }
            BufferedImage iconImage = getImage("https://assets2.lxns.net/maimai/icon/" + iconId +".png",true,200);
            g2d.drawImage(iconImage, 60 + 856 * row2, (566 + 10)* (i - row2*7) + 60   , null);

            BufferedImage nameImage = getImage(".//res//Name.png");
            g2d.drawImage(nameImage, 270+856 * row2, (566 + 10)* (i - row2*7) + 60, null);
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                    .deriveFont(Font.PLAIN, 44); // 设置字体样式和大小

            g2d.setFont(customFont); // 设置 Graphics2D 使用的字体
            g2d.setColor(Color.BLACK);
            int textX = 300 +856 * row2;
            int textY = (566 + 10)* (i - row2*7) + 110;
            g2d.drawString(username, textX, textY);



            BufferedImage ratingImage = null;
            if (rating >= 15000) {
                ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_11.png",60);
            }else if (rating >= 14500) {
                ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_10.png",60);
            } else if (rating >= 14000) {
                ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_9.png",60);
            } else if (rating >= 13000) {
                ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_8.png",60);
            } else if (rating>= 12000) {
                ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_7.png",60);
            } else if (rating >= 10000) {
                ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_6.png",60);
            } else  {
                ratingImage = getImageAndResize(".//res//UI_CMN_DXRating_1.png",60);
            }
            g2d.drawImage(ratingImage, startX + 80, (566 + 10)* (i - row2*7) + 154, null);
            if(r1==1) {
                BufferedImage r1Image = getImageAndResize(".//res//UI_NUM_Drating_1.png",35);
                g2d.drawImage(r1Image, startX + 82 + 136, (566 + 10)* (i - row2*7) +165, null);
            }
            if (rating >= 1000) {
                BufferedImage r2Image = getImageAndResize(".//res//UI_NUM_Drating_" + r2 + ".png",35);
                g2d.drawImage(r2Image, startX + 82 + 162 , (566 + 10)* (i - row2*7) +165, null);
            }
            if (rating >= 100) {
                BufferedImage r3Image = getImageAndResize(".//res//UI_NUM_Drating_" + r3 + ".png",35);
                g2d.drawImage(r3Image, startX + 82 + 187, (566 + 10)* (i - row2*7) +165, null);
            }
            if (rating >= 10) {
                BufferedImage r4Image = getImageAndResize(".//res//UI_NUM_Drating_" + r4 + ".png",35);
                g2d.drawImage(r4Image, startX + 82 + 212, (566 + 10)* (i - row2*7) +165, null);
            }
            if (rating >= 1) {
                BufferedImage r5Image = getImageAndResize(".//res//UI_NUM_Drating_" + r5 + ".png",35);
                g2d.drawImage(r5Image, startX + 82 + 237, (566 + 10)* (i - row2*7) +165, null);
            }

            if(i==0) {
                BufferedImage rankImage = getImageAndResize(".//res//UI_MSS_Allclear_Icon_SSSp.png",200);
                g2d.drawImage(rankImage, startX + 280, (566 + 10)* (i - row2*7) + 250, null);
            }else if(i==1) {
                BufferedImage rankImage = getImageAndResize(".//res//UI_MSS_Allclear_Icon_SSS.png",200);
                g2d.drawImage(rankImage, startX + 280, (566 + 10)* (i - row2*7) + 250, null);
            }else if(i==2) {
                BufferedImage rankImage = getImageAndResize(".//res//UI_MSS_Allclear_Icon_SSp.png",200);
                g2d.drawImage(rankImage, startX + 280, (566 + 10)* (i - row2*7) + 250, null);
            }else if (i==3) {
                BufferedImage rankImage = getImageAndResize(".//res//UI_MSS_Allclear_Icon_SS.png",200);
                g2d.drawImage(rankImage, startX +280, (566 + 10)* (i - row2*7) + 250, null);
            } else if (i==4) {
                BufferedImage rankImage = getImageAndResize(".//res//UI_MSS_Allclear_Icon_Sp.png",200);
                g2d.drawImage(rankImage, startX +280, (566 + 10)* (i - row2*7) + 250, null);
            } else if (i==5) {
                BufferedImage rankImage = getImageAndResize(".//res//UI_MSS_Allclear_Icon_S.png",200);
                g2d.drawImage(rankImage, startX + 280, (566 + 10)* (i - row2*7) + 250, null);
            } else  {
                Font text = Font.createFont(Font.TRUETYPE_FONT, new File(".//libs//AlibabaPuHuiTi3.0-75SemiBold-CJKTGv4.3.ttf"))
                        .deriveFont(Font.PLAIN, 80);
                g2d.setFont(text);
                Color textColor = new Color(255, 53, 53);
                g2d.setColor(textColor);
                g2d.drawString("TOP - " + (i + 1), startX + 210, (566 + 10)* (i - row2*7) + 300);
            }

        }
        g2d.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        return new ByteArrayInputStream(imageBytes);
    }

    public ByteArrayInputStream generateChart(UserMusicList userScore) throws IOException {
        BufferedImage backgroundImage = ImageIO.read(Paths.get(".//res//info_bg.png").toFile());
        int width = backgroundImage.getWidth();
        int height = backgroundImage.getHeight();

        Graphics2D g2d = backgroundImage.createGraphics();

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        List<MusicRating> musicRatingList = userScore.getUserMusicDetailList();
        int id = musicRatingList.get(0).getMusicId();
        if(id>10000) {
            id = id-10000;
        }
        BufferedImage iconImage = getImage("https://assets2.lxns.net/maimai/jacket/" + id +".png",true,600);
        g2d.drawImage(iconImage, 100, 340, null);

        Font font2 = new Font("Arial", Font.BOLD, 55);
        g2d.setFont(font2);
        g2d.setColor(Color.BLACK);
        g2d.drawString(musicRatingList.get(0).getMusicId()+"", 160, 1100);

        for (int i = 0; i < musicRatingList.size(); i++) {
            MusicRating musicRating = musicRatingList.get(i);
            if(musicRating.getLevel() == 0) {
                //书写
                {
                    // 添加文本
                    Font font = new Font("Arial", Font.BOLD, 55);
                    g2d.setFont(font);
                    g2d.setColor(Color.BLACK);
                    int textX = 800;
                    int textY = 530;
                    String ac = String.valueOf(musicRating.getAchievement());
                    //从后往前第5位加.
                    if(ac.length()>4) {
                        ac = ac.substring(0, ac.length() - 4) + "." + ac.substring(ac.length() - 4);
                    }
                    g2d.drawString(musicRating.getLevel_info()+"",textX + 180,textY - 60);

                    g2d.drawString(ac + "   --->  Point count: " + musicRating.getPlayCount(), textX, textY);
                }
            }
            if(musicRating.getLevel() == 1) {
                //书写
                {
                    // 添加文本
                    Font font = new Font("Arial", Font.BOLD, 55);
                    g2d.setFont(font);
                    g2d.setColor(Color.BLACK);
                    int textX = 800;
                    int textY = 650;
                    String ac = String.valueOf(musicRating.getAchievement());
                    //从后往前第5位加.
                    if(ac.length()>4) {
                        ac = ac.substring(0, ac.length() - 4) + "." + ac.substring(ac.length() - 4);
                    }
                    g2d.drawString(musicRating.getLevel_info()+"",textX + 180,textY - 60);

                    g2d.drawString(ac + "   --->  Point count: " + musicRating.getPlayCount(), textX, textY);
                }
            }
            if(musicRating.getLevel() == 2) {
                //书写
                {
                    // 添加文本
                    Font font = new Font("Arial", Font.BOLD, 55);
                    g2d.setFont(font);
                    g2d.setColor(Color.BLACK);
                    int textX = 800;
                    int textY = 770;
                    String ac = String.valueOf(musicRating.getAchievement());
                    //从后往前第5位加.
                    if(ac.length()>4) {
                        ac = ac.substring(0, ac.length() - 4) + "." + ac.substring(ac.length() - 4);
                    }
                    g2d.drawString(musicRating.getLevel_info()+"",textX + 180,textY - 60);

                    g2d.drawString(ac + "   --->  Point count: " + musicRating.getPlayCount(), textX, textY);
                }
            }
            if(musicRating.getLevel() == 3) {
                {
                    // 添加文本
                    Font font = new Font("Arial", Font.BOLD, 55);
                    g2d.setFont(font);
                    g2d.setColor(Color.BLACK);
                    int textX = 800;
                    int textY = 920;
                    String ac = String.valueOf(musicRating.getAchievement());
                    //从后往前第5位加.
                    if(ac.length()>4) {
                        ac = ac.substring(0, ac.length() - 4) + "." + ac.substring(ac.length() - 4);
                    }
                    g2d.drawString(musicRating.getLevel_info()+"",textX + 180,textY - 60);
                    g2d.drawString(ac + "   --->  Point count: " + musicRating.getPlayCount(), textX, textY);
                }
            }
            if(musicRating.getLevel() == 4) {
                {
                    // 添加文本
                    Font font = new Font("Arial", Font.BOLD, 55);
                    g2d.setFont(font);
                    g2d.setColor(Color.BLACK);
                    int textX = 800;
                    int textY = 1060;
                    String ac = String.valueOf(musicRating.getAchievement());
                    //从后往前第5位加.
                    if(ac.length()>4) {
                        ac = ac.substring(0, ac.length() - 4) + "." + ac.substring(ac.length() - 4);
                    }
                    g2d.drawString(musicRating.getLevel_info()+"",textX + 180,textY - 60);

                    g2d.drawString(ac + "   --->  Point count:  " + musicRating.getPlayCount(), textX, textY);
                }
            }
        }
        Font font = new Font("Arial", Font.BOLD, 50);
        g2d.setFont(font);
        g2d.drawString("FindMaimaiBot - PoweredBy Reisa", 420, 1260);

        //关闭
        g2d.dispose();

        // 将图像转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(backgroundImage, "png", baos);
        byte[] imageBytes = baos.toByteArray();
        ByteArrayInputStream imageStream = new ByteArrayInputStream(imageBytes);
        return imageStream;
    }
}
