/*
 * Copyright 2004-2013 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.bizreach.twitter.projectfw.web.paging;

import java.io.Serializable;
import java.util.List;

import org.seasar.dbflute.cbean.PagingResultBean;
import org.seasar.dbflute.cbean.pagenavi.PageNumberLink;
import org.seasar.dbflute.cbean.pagenavi.PageNumberLinkSetupper;
import org.seasar.dbflute.cbean.pagenavi.range.PageRangeOption;

/**
 * ページングナビゲーション構築オブジェクト。<br />
 * /WEB-INF/view/common/paging_navi.jsp を利用したページングの際に利用する。<br/>
 * このクラスのインスタンス変数名は pagingNavi であること。<br />
 * @author mokkouyou (initial making)
 * @author jflute (extends it)
 */
public class PagingNavi implements Serializable {

    private static final long serialVersionUID = 1L;

    // ===================================================================================
    //                                                                           Attribute
    //                                                                           =========
    public boolean displayPagingNavi;
    public int allRecordCount;
    public int allPageCount;
    public int currentPageNumber;
    public String prePageLinkHref;
    public String nextPageLinkHref;
    public boolean existPrePage;
    public boolean existNextPage;
    public List<PageNumberLink> pageNumberLinkList;
    public int pageNumberLinkIndex;

    // ===================================================================================
    //                                                                         Constructor
    //                                                                         ===========
    public PagingNavi() {
    }

    // ===================================================================================
    //                                                                                Main
    //                                                                                ====
    /**
     * Prepare paging navigation.
     * @param page The selected page as bean of paging result. (NotNull)
     * @param rangeOption The option of page range. (NotNull)
     * @param linkPaths The varying array of link paths. (NotNull, EmptyAllowed)
     */
    public void prepare(PagingResultBean<?> page, PageRangeOption rangeOption, Object... linkPaths) {
        displayPagingNavi = !page.isEmpty();

        allRecordCount = page.getAllRecordCount();
        currentPageNumber = page.getCurrentPageNumber();
        allPageCount = page.getAllPageCount();
        existPrePage = page.isExistPrePage();
        existNextPage = page.isExistNextPage();

        nextPageLinkHref = createTargetPageNumberLink(currentPageNumber + 1, linkPaths);
        prePageLinkHref = createTargetPageNumberLink(currentPageNumber - 1, linkPaths);
        pageNumberLinkList = createPageNumberLinkList(page, rangeOption, linkPaths);
    }

    /**
     * Create the list of page number link. <br />
     * @param page The bean of paging result. (NotNull)
     * @param rangeOption The option of page range. (NotNull)
     * @param linkPaths The array of like paths. (NotNull)
     * @return The list of page number link. (NotNull)
     */
    protected List<PageNumberLink> createPageNumberLinkList(PagingResultBean<?> page, PageRangeOption rangeOption,
            final Object[] linkPaths) {
        page.setPageRangeOption(rangeOption);
        PageNumberLinkSetupper<PageNumberLink> setupper = new PageNumberLinkSetupper<PageNumberLink>() {
            public PageNumberLink setup(int pageNumberElement, boolean current) {
                String targetPageNumberLink = createTargetPageNumberLink(pageNumberElement, linkPaths);
                return new PageNumberLink().initialize(pageNumberElement, current, targetPageNumberLink);
            }
        };
        return page.pageRange().buildPageNumberLinkList(setupper);
    }

    /**
     * Create target page number link.
     * @param pageNumber Target page number.
     * @param linkPaths The array of like paths. (NotNull)
     * @return The link expression for target page. (NotNull)
     */
    protected String createTargetPageNumberLink(int pageNumber, Object[] linkPaths) {
        StringBuilder sb = new StringBuilder();
        if (linkPaths != null && linkPaths.length > 0) {
            for (Object path : linkPaths) {
                sb.append(path).append("/");
            }
        }
        return sb.toString() + pageNumber + "/";
    }
}
