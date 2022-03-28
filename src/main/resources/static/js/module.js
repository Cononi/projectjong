function SearchInfoController() {
    const searchMainForm = document.getElementById("searchForms")
    const children = Array.from(searchMainForm.children)
    const inputList = document.getElementById('searchmodal')
    const parserCountryList = JSON.parse(localStorage.getItem("ctl"));

    children.forEach(child => {
        if (!child.value) {
            child.setAttribute("disabled", "disabled")
        }
    })

    // 검색창 체크
    function searchInputText() {
        const queryInput = document.getElementById("query")
        // let boolCheck = false
        document.querySelectorAll("input[name=queryInput]").forEach(querys =>
            querys.addEventListener('keyup', function () {
                if (querys.value.length > 0) {
                    queryInput.value = querys.value;
                    queryInput.removeAttribute('disabled')
                } else {
                    queryInput.value = "";
                    queryInput.setAttribute("disabled", "disabled")
                }
            }))
    }


    // 나라 데이터 
    function searchCountryFetch() {
        if (!parserCountryList) {
            fetch("/api/find/country")
                .then((response) => response.json())
                .then((countryData) => {
                    let countryList = [];
                    for (const data of countryData) {
                        countryList.push({ label: data.countryKo, value: data.countryId })
                    }
                    localStorage.setItem("ctl", JSON.stringify(countryList))
                });
        }


        // 자동완성
        const field = document.getElementById('countryInput')
        const fieldCountry = document.getElementById('country')
        const ac = new Autocomplete(field, {
            data: parserCountryList,
            maximumItems: 5,
            threshold: 1,
            onSelectItem: ({ label, value }) => {
                field.value = label
                fieldCountry.value = value
                fieldCountry.removeAttribute('disabled')
            }
        });

        // 필드
        field.addEventListener('input', function () {
            let lastLen = field.value.slice(-1, field.value.length)
            let firstLen = field.value.slice(field.value.length - 2, field.value.length - 1)
            if (field.value.length > 0) {
                fieldCountry.value = field.value
                fieldCountry.removeAttribute('disabled')
            } else {
                fieldCountry.value = ""
            }
            if (lastLen == firstLen && field.value.length > 1) {
                field.value = field.value.slice(0, -1)
                this.blur()
                this.focus()
            }
        })

        // 데이타 이름변환
        const searchSpanList = document.querySelector('#searchSpanList')
        if (searchSpanList)
            searchSpanList.querySelectorAll('span b').forEach(e => {
                parserCountryList.forEach(country => {
                    if (country.value == e.innerText) {
                        e.innerText = country.label
                        field.value = country.label
                        return;
                    }
                })
            });
    }


    // 검색창
    function submitForm() {
        const searchForm = document.querySelectorAll("form.input-group")
        searchForm.forEach(e => e.addEventListener('submit', e => {
            e.preventDefault()
            searchMainForm.submit()
        }))
    }

    function modalForm() {
        const modalFormInput = document.getElementById("modalInputCheck")
        modalFormInput.addEventListener('click', () => {
            searchMainForm.submit()
        })
    }


    // 상세 검색 속성 체크
    function searchConditonCheck() {
        const searchDetails = document.querySelectorAll("input~label")
        // let searchChecked = document.querySelectorAll("input[checked]")
        // searchChecked.forEach(check => {
        //     searchCheck(check.name,check.value)
        // })
        searchDetails.forEach(details => details.addEventListener('click', function () {
            details.previousElementSibling.setAttribute('checked', false)
            searchCheck(details.previousElementSibling.name, details.previousElementSibling.value)
        }));
    }


    // 검색 초기화
    function deselect() {
        document.getElementById("none").addEventListener('click', () => {
            let searchCheckList = Array.from(inputList.getElementsByTagName('input'))
            deselectFor(searchCheckList, children)
        });
    };


    // 타입 체킹
    function spanTypeCreateList() {
        if (window.location.pathname == "/wine") {
            children.forEach(e => {
                if (e.value && e.name != "type" && e.name != "page" && e.name != "query") {
                    let spanTypeCreate = document.createElement("span");
                    spanTypeCreate.setAttribute("class", "shadow-sm mx-1 input-group-text bg-white col-auto py-1 fw-bold")
                    spanTypeCreate.innerText = typeInputCheck(e)
                    searchSpanList.prepend(spanTypeCreate);
                }
            })
        }
    }




    // 문자 변환

    /*
        내부 변수 모집
    */
    // 체크 인풋
    const inputPlaceHolderText = document.getElementById("queryInfoName")
    const searchCheck = function searchChecked(id, value) {
        let holderMsg = ""
        children.forEach(child => {
            if (child.id == id) {
                child.value = value;
                child.removeAttribute("disabled")
                if (child.value == "producerName") {
                    holderMsg = "영문명으로 생산자 입력"
                }
                if (child.value == "contents") {
                    holderMsg = "와인 내용으로 검색"
                }
                inputPlaceHolderText.placeholder = holderMsg
            }
        })
    }


    // 타입 도출
    const field = document.getElementById('countryInput')
    const typeInputCheck = (type) => {
        let valueLabel
        switch (type.id) {
            case "country":
                parserCountryList.forEach(e => {
                    if (e.value == type.value) {
                        valueLabel = e.label
                        // 인풋
                        field.value = e.label
                    }
                })
                break
            case "colour":
                if (type.value == "red")
                    valueLabel = "레드"
                else if (type.value == "white")
                    valueLabel = "화이트"
                else if (type.value == "rose")
                    valueLabel = "로제"
                break
            case "attr":
                if (type.value == "producerName")
                    valueLabel = "생산자"
                else if (type.value == "contents")
                    valueLabel = "내용"
                break
            case "subType":
                if (type.value == "sparkling")
                    valueLabel = "스파클링"
                else if (type.value == "port")
                    valueLabel = "포트"
                break
        }
        return valueLabel
    }

    // 검색 초기화
    const deselectFor = function deselectFor(searchCheckList, children) {
        const inputNotQuery = document.getElementById("queryInput")
        searchCheckList.forEach(e => { // 원래 초기화
            if (e.name == 'queryInput' || e.name == "country") {
                e.value = ""
            }
            e.checked = false
        });
        children.forEach(e => { // 메인 초기화
            if (e.name != 'type' && e.name != 'page') {
                e.value = "";
                e.setAttribute('disabled', 'disabled')
                inputPlaceHolderText.placeholder = "영문명 또는 한글명"
            }
        });
    }

    return {
        input: searchInputText(),
        submit: submitForm(),
        check: searchConditonCheck(),
        deselect: deselect(),
        searchCountryFetch: searchCountryFetch(),
        spanTypeCreateList: spanTypeCreateList(),
        modalForm: modalForm()
    }
}

export { SearchInfoController }


//----------------------------------------------------------------//

function tastingPostSubmitContents(methods, postNum) {

    // 기본 문자들
    const titleValueText = document.getElementById("tastingTitle")
    const ContentsValueText = document.getElementById("tastingContents")
    const vintageValueText = document.getElementById("tastingVintage")
    const alcoholValueText = document.getElementById("tastingAlcohol")
    const priceValueText = document.getElementById("tastingPrice")
    const tastingBtnSumbit = document.getElementById("tastingBtnSumbit")
    const itemIdSet = document.querySelector("span[data-columns]")

    // 토탈 별 체크 레이팅
    const rangeValueBtt = document.getElementById("userTotalRagneValue")
    const rangeValueText = document.getElementById("totalCountValue")
    const starPointIcon = document.querySelectorAll("#starPointIcon i")


    function rangeInputChange() {
        rangeValueText.innerText = rangeValueBtt.value + " / 100"
        let rangeV = rangeValueBtt.value / 20
        for (i = starPointIcon.length; 0 <= i; i--) {
            if (Math.floor(rangeV) > i)
                starPointIcon[i].classList = "bi bi-star-fill fa-2x"
            else if (rangeV >= i + 0.5)
                starPointIcon[i].classList = "bi bi-star-half fa-2x"
            else if (i != 0)
                starPointIcon[i - 1].classList = "bi bi-star fa-2x"
        }
    }
    // 변경시
    rangeValueBtt.oninput = function () {
        rangeInputChange()
    }
    // 초기값
    rangeInputChange()

    // 별 체크 레이팅
    const acidityRate = document.getElementsByName("acidityEat")
    const bodyRate = document.getElementsByName("bodyEat")
    const sugarRate = document.getElementsByName("sugarEat")

    function rateCheckItem(item) {
        let itemNum = ""
        item.forEach(e => { if (e.checked) itemNum = e.value })
        return itemNum
    }

    tastingBtnSumbit.addEventListener('click', async (e) => {
        // e.target.setAttribute("disabled", "disabled")
        let numberInfo = await tastingPostCall()
        if (numberInfo != null)
            location.href = "/post/info/" + numberInfo + "/1"
    })

    function tastingPostCall() {
        let url = '/api/v1/post'
        return fetch(url, {
            method: methods,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                idPostData: postNum,
                titPostData: titleValueText.value,
                conPostData: ContentsValueText.value,
                vinPostData: vintageValueText.value,
                alcPostData: alcoholValueText.value,
                aciPostData: rateCheckItem(acidityRate),
                bodyPostData: rateCheckItem(bodyRate),
                sugPostData: rateCheckItem(sugarRate),
                scPostData: rangeValueBtt.value,
                priPostData: priceValueText.value,
                winePostData: itemIdSet.dataset.columns
            }),
        }).then(function (response) {
            if (!response.ok) {
                let error = response
                error.then(e => {
                    // 오류 처리.
                    console.log(e)
                })
            } else
                console.log(response)
            if (response.url.includes('/login')) {
                location.href = "/account/login";
            } else {
                return response.json()
            }
        });
    }
}

export { tastingPostSubmitContents }

// 삭처리
export function postInfoPageFet(num, pages) {
    let tastingDeSumbit = document.getElementById("delCheckBtt")

    tastingDeSumbit.addEventListener('click', async () => {
        await infoPostDeCall()
        // 마페로감
        location.href = pages
    })

    function infoPostDeCall() {
        let url = '/api/v1/post/' + num
        fetch(url, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' },
        }).then(function (response) {
            if (!response.ok) {
                let error = response
                error.then(() => {
                    location.href = pages
                })
            } else {
                formExpCheck(response)
            }
        });
    }
}

// 만료첵
function formExpCheck(e) {
    if (e.url.includes('/login')) {
        location.href = "/account/login"
    } else {
        return e.json()
    }
}


// 테이스팅 페이지 ( 중복해서 쓰임으로 url 전달을 수정하도록 해야함.)
async function postListWineTasting(page, id, link) {
    let url = '/api/v1/post/' + page + '/' + link + '/' + id
    return await fetch(url)
        .then(e => e.json())
        .then(json => {
            const tableBodyEl = document.getElementById("userTastingTableBody")
            const tableTrEl = document.createElement("tr")


            if (json.totalElements != 0) {
                removeAllchild(tableBodyEl)
                for (i = 0; i < json.content.length; i++) {
                    let numberCount = ((json.totalElements - (json.pageable.pageNumber * 5)) - (i))
                    tableTrEl.innerHTML = `
                        <td>
                            <div class="row justify-content-md-center align-self-center">
                                <div class="row">
                                    <b class="col-md-2"><i class="fa-solid fa-book mx-1"></i>`+ numberCount + `</b>
                                    <p class="col-md-6 font-bold mb-0">`+ (json.content[i].title.slice(0, 40)) + (json.content[i].title.length > 40 ? '...' : '') + `</p>
                                    <div class="col-md-4">
                                        <div class="row">
                                            <p class="col-7 mb-0">`+ json.content[i].userId + `</p>
                                            <p class="col-5 text-center mb-0">`+ json.content[i].modifieDate + `</p>
                                        </div>
                                     </div>
                                </div>
                            </div>
                        </td>
                    `
                    console.log(json.content[i])
                    // 번호 등록
                    tableTrEl.setAttribute('data-columnNum', json.content[i].postId)
                    // 공동
                    tableBodyEl.appendChild(tableTrEl.cloneNode(true));
                }
                // 컨텐츠 이동
                tableBodyEl.querySelectorAll('tr[data-columnNum]').forEach(e => { // 공통
                    e.addEventListener('click', () => {
                        location.href = "/post/info/" + e.dataset.columnnum + "/" + (json.pageable.pageNumber + 1)
                    })
                })
                pageNavDataSet('wineInfoPagelistNavBar', postListWineTasting, id, json, link)
            } else {
                createNoneMessageAllchild(document.getElementById("headTasting"), tableTrEl, tableBodyEl, `<b>등록된 테이스팅 노트가 없습니다.</b>`)
            }

        });
}

// 마이 포스팅
async function postListMyTasting(num) {
    let url = '/api/v1/account/post/wine/' + num
    return await fetch(url)
        .then(e => formExpCheck(e))
        .then(json => {
            const tastingDivEl = document.getElementById("mytastingDiv")
            const tastingDivCard = document.createElement("div")
            const tastingMessageDiv = document.getElementById("sectiontastingMassage")

            if (json.totalElements != 0) {
                tastingDivCard.setAttribute("class", "card box-shadow mx-1 my-3 text-center")
                tastingDivCard.setAttribute("style", "width: 9em; height: 12em;")
                tastingDivCard.setAttribute("data-bs-toggle", "modal")
                tastingDivCard.setAttribute("data-bs-target", "#itemBoardModalList")

                removeAllchild(tastingDivEl)
                if (tastingMessageDiv != null) {
                    tastingMessageDiv.remove()
                }
                for (let i = 0; i < json.numberOfElements; i++) {
                    tastingDivCard.innerHTML = `
                <img src="/assets/images/samples/krug.jpg" class="card-img-top" alt="...">
                <p class="card-text mt-3 py-2 h6 small">`+ (json.content[i].displayNameKo.substring(0, 20)) + (json.content[i].displayNameKo.length > 20 ? '...' : '') + `</p>
                `
                    // 번호
                    tastingDivCard.setAttribute("data-columnNum", json.content[i].wineId)
                    //  공동
                    tastingDivEl.appendChild(tastingDivCard.cloneNode(true));
                }
                // 컨텐츠 이동
                tastingDivEl.querySelectorAll('div[data-columnNum]').forEach(e => { // 공통
                    e.addEventListener('click', () => {
                        postListWineTasting(1 , e.dataset.columnnum, 'wine')
                    })
                })
                pageNavDataSet('tastingPageList', postListMyTasting, 0, json)
            } else {
                createNoneMessageAllchild(document.getElementById("topContentBody"), tastingDivCard, document.getElementById("testingMassage"), `<b>지금 바로 포스팅을 작성해보세요!.</b>`)
            }
        })
}

export { postListWineTasting, postListMyTasting }



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
        for (i = firstpage; i < maxPage; i++) {
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
                if (customerData.length == 3)
                    customerData(e.dataset.pagenum, ItemPageId, link) // 공통
                else
                    customerData(e.dataset.pagenum)
            })
    })
    tableNav.appendChild(tableNavUl)
}


// 추가 함수
function createNoneMessageAllchild(topBody, center, parent, ment) { // 메인,중앙,부모
    topBody.remove()
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


