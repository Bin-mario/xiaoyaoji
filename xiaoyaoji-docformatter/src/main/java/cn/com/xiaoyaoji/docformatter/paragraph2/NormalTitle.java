package cn.com.xiaoyaoji.docformatter.paragraph2;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

public class NormalTitle extends Chapter {

    private static final long serialVersionUID = 5366051313428343718L;

    private static final float DEFAULT_MARGIN = 17.333F;

    private Font font = new Font(BaseInfo.cjkFont, 16f, Font.BOLD, BaseColor.BLACK);

    public NormalTitle(String title, int number) {
        super(number);
        Paragraph titlePara = new Paragraph(title, font);
        titlePara.setSpacingAfter(DEFAULT_MARGIN);
        titlePara.setSpacingBefore(DEFAULT_MARGIN);
        setTitle(titlePara);
        setTriggerNewPage(false);
    }
}
