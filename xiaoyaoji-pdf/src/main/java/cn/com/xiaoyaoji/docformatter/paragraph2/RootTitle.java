package cn.com.xiaoyaoji.docformatter.paragraph2;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

public class RootTitle extends Paragraph {

	private static final long serialVersionUID = 2774720437959757169L;

	public RootTitle(Document doc, String title) {
		Paragraph placeHolder = new Paragraph("\n");
		placeHolder.setSpacingAfter(doc.getPageSize().getHeight() * 0.1f);
		add(placeHolder);
		Paragraph p = new Paragraph(title, new Font(BaseInfo.cjkFont, 24f, Font.BOLD, BaseColor.BLACK));
		p.setAlignment(ALIGN_CENTER);
		p.setSpacingAfter(100);
		add(p);
	}
}
