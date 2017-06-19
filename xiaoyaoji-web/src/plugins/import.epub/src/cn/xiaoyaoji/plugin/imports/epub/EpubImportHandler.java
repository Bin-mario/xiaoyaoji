package cn.xiaoyaoji.plugin.imports.epub;

import cn.com.xiaoyaoji.plugins.imports.ImportHandler;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Guide;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @author zhoujingjie
 *         created on 2017/6/17
 */
public class EpubImportHandler implements ImportHandler {
    public static void main(String[] args) throws IOException {
        EpubReader reader = new EpubReader();
        Book book = reader.readEpub(new FileInputStream("f://Mybatis 实战教程 - v1.0.epub"));
        Guide guide = book.getGuide();
        Resource coverPage = guide.getCoverPage();

        BufferedImage coverImage = ImageIO.read(new ByteArrayInputStream(book.getCoverImage().getData()));
        ImageIO.write(coverImage,"jpeg",new File("F:\\mybatis实战\\cover.jpg"));
        String data = new String(coverPage.getData(),coverPage.getInputEncoding());
        System.out.println(data);
    }
}
