let setCookie = function(name, value) {
	var date = new Date();
	document.cookie = name + '=' + value + ';path=/';
};

// 추가 함수
function createNoneMessageAllchild(topBody, center, parent, ment) { // 메인,중앙,부모
    removeAllchild(topBody)
    center.innerHTML = ment
    center.setAttribute('class', "text-center my-4")
    parent.appendChild(center)
}

// 제거 함수
function removeAllchild(div) {
    while (div.hasChildNodes()) {
        div.removeChild(div.firstChild);
    }
}

// 공동 페이지 (테이블 data-columnNum, 페이징 data-pagenum)
function pageNavDataSet(MainELId, customerData, ItemPageId, json, link) { //MainElDataSet (테이블 데이터 셋), MainElId (메인 El Id), MainEl (메인 El), customerData (객체 전달),ItemPageId(페이지 아이템 관련)  json(Json데이터)

    // 페이징
    const tableNav = document.getElementById(MainELId) // 공통
    const tableNavUl = document.createElement("ul")
    removeAllchild(tableNav)
    tableNavUl.setAttribute('class', 'pagination pagination-danger justify-content-center')
    tableNavUl.setAttribute('id', 'navUiTop')

    let firstpage = Math.floor((json.pageable.pageNumber) / 5) * 5 + 1
    let lastpage = (firstpage + 5)
    let pageIno = ''
    let pageLano = ''
    if (firstpage > 5) {
        pageIno = `<a class="page-link" data-pagenum=` + (firstpage - 1) + `><span aria-hidden="true"><i
            class="bi bi-chevron-left"></i></span></a>`
    }
    tableNavUl.innerHTML = `
        <li class="page-item">`+ pageIno + `</li>
        `
    if (json.totalElements != 0) {
        let maxPage = lastpage > json.totalPages ? json.totalPages + 1 : lastpage
        for (let i = firstpage; i < maxPage; i++) {
            let pageActive = ''
            if (json.pageable.pageNumber + 1 == i) {
                pageActive = "active"
            }
            tableNavUl.innerHTML += `
            <li class="page-item `+ pageActive + `"><a class="page-link" data-pagenum=` + (i) + `>` + i + `</a></li>
            `
        }
    }
    if (lastpage <= json.totalPages) {
        pageLano = `<a class="page-link" data-pagenum=` + lastpage + `><span aria-hidden="true">
        <i class="bi bi-chevron-right"></i></span></a>`
    }
    tableNavUl.innerHTML += `
        <li class="page-item">` + pageLano + `
        </li>
`
    tableNavUl.querySelectorAll('a[data-pagenum]').forEach(e => {
        if (!e.parentElement.classList.contains('active'))
            e.addEventListener('click', () => {
                if (customerData.length == 3) {
                    customerData(e.dataset.pagenum, ItemPageId, link) // 공통
                    thisCommentCountNum = e.dataset.pagenum // 코멘트 카운트용
                } else if (customerData.length == 2) {
                    customerData(ItemPageId, e.dataset.pagenum)
                } else {
                    customerData(e.dataset.pagenum)
                    window.scrollTo({ top: 0, behavior: 'smooth' });
                }
                tableNavUl.scrollIntoView()
            })
    })
    tableNav.appendChild(tableNavUl)
}

export {setCookie, createNoneMessageAllchild, removeAllchild, pageNavDataSet}