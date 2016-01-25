package blanco.xls2calendar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import blanco.commons.util.BlancoStringUtil;
import blanco.xls2calendar.util.BlancoXls2CalendarUtil;
import blanco.xml.bind.BlancoXmlBindingUtil;
import blanco.xml.bind.BlancoXmlUnmarshaller;
import blanco.xml.bind.valueobject.BlancoXmlDocument;
import blanco.xml.bind.valueobject.BlancoXmlElement;

public class BlancoXls2CalendarXmlParser {
    /**
     * 中間XMLファイルのXMLドキュメントをパースして、情報の配列を取得します。
     * 
     * @param argMetaXmlSourceFile
     *            中間XMLファイル。
     * @return パースの結果得られた情報の配列。
     */
    public BlancoXls2CalendarStructure[] parse(final File argMetaXmlSourceFile) {
        final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller().unmarshal(argMetaXmlSourceFile);
        if (documentMeta == null) {
            return null;
        }

        return parse(documentMeta);
    }

    public BlancoXls2CalendarStructure[] parse(final byte[] arg) {
        final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller().unmarshal(new ByteArrayInputStream(arg));

        if (documentMeta == null) {
            return null;
        }

        return parse(documentMeta);
    }

    /**
     * 中間XMLファイル形式のXMLドキュメントをパースして、バリューオブジェクト情報の配列を取得します。
     * 
     * @param argXmlDocument
     *            中間XMLファイルのXMLドキュメント。
     * @return パースの結果得られたバリューオブジェクト情報の配列。
     */
    public BlancoXls2CalendarStructure[] parse(final BlancoXmlDocument argXmlDocument) {
        final List<BlancoXls2CalendarStructure> listStructure = new ArrayList<BlancoXls2CalendarStructure>();
        // ルートエレメントを取得します。
        final BlancoXmlElement elementRoot = BlancoXmlBindingUtil.getDocumentElement(argXmlDocument);
        if (elementRoot == null) {
            // ルートエレメントが無い場合には処理中断します。
            return null;
        }

        // sheet(Excelシート)のリストを取得します。
        final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil.getElementsByTagName(elementRoot, "sheet");
        final int sizeListSheet = listSheet.size();
        for (int index = 0; index < sizeListSheet; index++) {
            final BlancoXmlElement elementSheet = listSheet.get(index);

            final BlancoXls2CalendarStructure structure = parseElementSheet(elementSheet);
            if (structure != null) {
                // 得られた情報を記憶します。
                listStructure.add(structure);
            }
        }

        final BlancoXls2CalendarStructure[] result = new BlancoXls2CalendarStructure[listStructure.size()];
        listStructure.toArray(result);
        return result;
    }

    /**
     * 中間XMLファイル形式の「sheet」XMLエレメントをパースして、バリューオブジェクト情報を取得します。
     * 
     * @param argElementSheet
     *            中間XMLファイルの「sheet」XMLエレメント。
     * @return パースの結果得られたバリューオブジェクト情報。「name」が見つからなかった場合には nullを戻します。
     */
    public BlancoXls2CalendarStructure parseElementSheet(final BlancoXmlElement argElementSheet) {
        final BlancoXls2CalendarStructure structure = new BlancoXls2CalendarStructure();
        // 入力パラメータ情報を取得します。

        final List<BlancoXmlElement> listCommon = BlancoXmlBindingUtil.getElementsByTagName(argElementSheet,
                "blancoxls2calendar-common");
        if (listCommon.size() == 0) {
            // commonが無い場合にはスキップします。
            return null;
        }

        // 最初のアイテムのみ処理しています。
        final BlancoXmlElement elementCommon = listCommon.get(0);

        // シートから詳細な情報を取得します。
        structure.setEmailaddr(BlancoXmlBindingUtil.getTextContent(elementCommon, "emailaddr"));
        structure.setUid(BlancoXmlBindingUtil.getTextContent(elementCommon, "uid"));

        if (BlancoStringUtil.null2Blank(structure.getEmailaddr()).length() == 0) {
            return null;
        }

        if (BlancoStringUtil.null2Blank(structure.getUid()).trim().length() == 0) {
            // FIXME abort if uid eq blank.
            return null;
        }

        final BlancoXmlElement elementEventList = BlancoXmlBindingUtil.getElement(argElementSheet,
                "blancoxls2calendar-list");
        if (elementEventList == null) {
            return null;
        }

        // 一覧の内容を取得します。
        final List<BlancoXmlElement> listEvent = BlancoXmlBindingUtil.getElementsByTagName(elementEventList, "event");
        for (int indexField = 0; indexField < listEvent.size(); indexField++) {
            final BlancoXmlElement elementField = listEvent.get(indexField);

            final BlancoXls2CalendarEventStructure eventStructure = new BlancoXls2CalendarEventStructure();

            eventStructure.setNo(BlancoXmlBindingUtil.getTextContent(elementField, "no"));
            eventStructure.setTitle(BlancoXmlBindingUtil.getTextContent(elementField, "title"));
            if (BlancoStringUtil.null2Blank(eventStructure.getTitle()).length() == 0) {
                // titleが指定されていない場合には処理しません。
                continue;
            }

            if (BlancoStringUtil.null2Blank(BlancoXmlBindingUtil.getTextContent(elementField, "description"))
                    .length() > 0) {
                eventStructure.setDescription(BlancoXmlBindingUtil.getTextContent(elementField, "description"));
            }

            if (BlancoStringUtil.null2Blank(BlancoXmlBindingUtil.getTextContent(elementField, "location"))
                    .length() > 0) {
                eventStructure.setLocation(BlancoXmlBindingUtil.getTextContent(elementField, "location"));
            }

            if (BlancoStringUtil.null2Blank(BlancoXmlBindingUtil.getTextContent(elementField, "ymd")).length() > 0) {
                final Date date = parseDate(BlancoXmlBindingUtil.getTextContent(elementField, "ymd"));
                final Date dateBeginPart = parseTime(BlancoXmlBindingUtil.getTextContent(elementField, "beginHm"));
                final Date dateEndPart = parseTime(BlancoXmlBindingUtil.getTextContent(elementField, "endHm"));
                eventStructure.setDateBegin(BlancoXls2CalendarUtil.newDate(date.getYear(), date.getMonth() + 1,
                        date.getDate(), dateBeginPart.getHours(), dateBeginPart.getMinutes()));
                eventStructure.setDateEnd(BlancoXls2CalendarUtil.newDate(date.getYear(), date.getMonth() + 1,
                        date.getDate(), dateEndPart.getHours(), dateEndPart.getMinutes()));
            }

            // TODO 既に同じ内容が登録されていないかどうかのチェック。

            structure.getEventList().add(eventStructure);
        }

        return structure;
    }

    private Date parseDate(final String arg) {
        if (arg == null) {
            return null;
        }
        try {
            final SimpleDateFormat sdFormat = new SimpleDateFormat("yy/MM/dd");
            return sdFormat.parse(arg);
        } catch (ParseException e) {
            // TODO 例外処理の実装。
            e.printStackTrace();
            return null;
        }
    }

    private Date parseTime(final String arg) {
        if (arg == null) {
            return null;
        }
        try {
            final SimpleDateFormat sdFormat = new SimpleDateFormat("HH:mm");
            return sdFormat.parse(arg);
        } catch (ParseException e) {
            // TODO 例外処理の実装。
            e.printStackTrace();
            return null;
        }
    }
}
