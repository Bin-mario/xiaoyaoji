package cn.com.xiaoyaoji.docformatter.paragraph2;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

public class NormalSubTitle extends Paragraph {

    private static final long serialVersionUID = -1937257304645658682L;

    private static final float DEFAULT_MARGIN = 10F;

    private Font font = new Font(BaseInfo.cjkFont, 14f, Font.BOLD, BaseColor.BLACK);

    public NormalSubTitle(NormalTitle parent, String title, int index) {

        setFont(font);
        setSpacingAfter(DEFAULT_MARGIN);
        setSpacingBefore(DEFAULT_MARGIN);
        add(title);

        if (parent != null) {
            parent.addSection(index, this);
        }
    }

    public NormalSubTitle(Paragraph parent, String title, int index) {

        setFont(font);
        Paragraph titlePara = new Paragraph(title, font);
        titlePara.setSpacingAfter(DEFAULT_MARGIN);
        titlePara.setSpacingBefore(DEFAULT_MARGIN);
        add(titlePara);

        if (parent != null) {
            parent.add(index, this);
        }
    }

    public NormalSubTitle(Paragraph parent, String title, int index, float vMargin) {

        setFont(font);
        Paragraph titlePara = new Paragraph(title, font);
        titlePara.setSpacingAfter(vMargin);
        titlePara.setSpacingBefore(vMargin);
        add(titlePara);

        if (parent != null) {
            parent.add(index, this);
        }
    }
}
