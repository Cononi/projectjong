import { setCookie, createNoneMessageAllchild, removeAllchild, pageNavDataSet} from '/js/common.js';

let con = true
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
        if (searchSpanList) {
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
    }


    // 검색창
    function timeSubMitCon() {
        let timeSubLodingImg = document.getElementById("lodingImg")
        timeSubLodingImg.removeAttribute("class")
        con = false
        setTimeout(function () {
            timeSubLodingImg.setAttribute("class", "d-none")
            wineSearchFind(0)
        }, 1000);
        //지연
    }

    function submitForm() {
        const searchForm = document.getElementById('queryInput')
        searchForm.addEventListener('keydown', (e) => {
            if (e.keyCode === 13 && con) {
                timeSubMitCon()
            }
        })
    }

    function modalForm() {
        const modalFormInput = document.getElementById("modalInputCheck")
        modalFormInput.addEventListener('click', () => {
            if (con) {
                spanTypeCreateList()
                timeSubMitCon()
            }
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
        removeAllchild(searchSpanList)
        if (window.location.pathname == "/wine") {
            children.forEach(e => {
                if (e.disabled == false) {
                    if (e.value && e.name != "type" && e.name != "page" && e.name != "query") {
                        let spanTypeCreate = document.createElement("span");
                        spanTypeCreate.setAttribute("class", "shadow-sm mx-1 input-group-text bg-white col-auto py-1 fw-bold")
                        spanTypeCreate.innerText = typeInputCheck(e)
                        searchSpanList.prepend(spanTypeCreate);
                    }
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



// -----------------------------------------------------------------
// 검색
 function wineSearchFind(pagenum) {
    const searchMainForm = document.getElementById("searchForms")
    const children = Array.from(searchMainForm.children)
    const mainContentChangeDivEl = document.getElementById("mainContentChangeDiv")
    const mainContentDivCard = document.createElement("div")
    const tastingMessageDiv = document.getElementById("sectiontastingMassage")
    const searchBodyCard = document.getElementById("searchMsgBodyCard")
    searchBodyCard.removeAttribute("class")

    // 메세지 제거
    removeAllchild(searchBodyCard)
    var object = {};
    children.forEach(function (value) {
        object[value.name] = value.value;
    });
    if (pagenum > 0) { object['page'] = pagenum }
    let url = '/api/find/wine'
    fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(object),
    })
        .then(e => e.json())
        .then(json => {
            if (json.totalElements != 0) {
                mainContentDivCard.setAttribute("class", "card box-shadow mx-auto px-auto")
                mainContentDivCard.setAttribute("style", "width: 16rem;")
                mainContentDivCard.setAttribute("th:data-columns", "modal")
                if (tastingMessageDiv != null) {
                    tastingMessageDiv.remove()
                }
                // 메인 제거
                removeAllchild(mainContentChangeDivEl)
                for (let i = 0; i < json.numberOfElements; i++) {
                    let starList = ''
                    for (let j = 1; j <= 5; j++) {
                        starList += `<i style="color: #4249a9" class="bi` + (json.content[i].averageScore / 20 >= j ? ' bi-star-fill ' : ((json.content[i].averageScore % 20 != 0) && ((json.content[i].averageScore / 20) + 1) >= j ? ' bi-star-half ' : ' bi-star ')) + `fs-5"></i>`
                    }
                    mainContentDivCard.innerHTML = `
                    <img src="` + json.content[i].wineImageUrl + `" class="card-img-top" alt="...">
                    <div class="card-body px-2">
                        <h6 style='color: #2b68e3'>`+ json.content[i].averageScore + ' / 100' + `</h6>
                        `+ starList + `
                        <h5 class="card-title">` + json.content[i].displayName + `</h5>
                        <h6 class="card-sub-title">` + json.content[i].displayNameKo + `</h6>
                        <hr>
                        <p class="card-text">`+ json.content[i].contents + `</p>
                    </div>
    `
                    // 번호
                    mainContentDivCard.setAttribute("data-columnNum", json.content[i].wineId)
                    //  공동
                    mainContentChangeDivEl.appendChild(mainContentDivCard.cloneNode(true));
                    con = true
                }
                // 컨텐츠 이동
                mainContentChangeDivEl.querySelectorAll('div[data-columnNum]').forEach(e => { // 공통
                    e.addEventListener('click', () => {
                        location.href = "wine/" + (json.pageable.pageNumber + 1) + "/" + e.dataset.columnnum + "/" + 1
                    })
                })
                pageNavDataSet('wineListPageList', wineSearchFind, 0, json)
            } else {
                const searchMsgDiv = document.getElementById("wineSearchMsg")
                removeAllchild(mainContentChangeDivEl)
                removeAllchild(document.getElementById('wineListPageList'))
                createNoneMessageAllchild(mainContentChangeDivEl, searchBodyCard, searchMsgDiv, `
                <div class="card-header">
                <h4 class="card-title">메세지</h4>
                </div>
                <div class="card-body"><b>검색 결과가 존재하지 않습니다.</b>
                </div>`)
                con = true
            }
        }).catch(() => {
            const searchMsgDiv = document.getElementById("wineSearchMsg")
            removeAllchild(mainContentChangeDivEl)
            removeAllchild(document.getElementById('wineListPageList'))
            createNoneMessageAllchild(mainContentChangeDivEl, searchBodyCard, searchMsgDiv, `
            <div class="card-header">
            <h4 class="card-title">메세지</h4>
            </div>
            <div class="card-body"><b>최소 검색 단어는 2글자 이상입니다.</b>
            </div>`)
            con = true
        })
}

export function passFindEmail() {

    // const findForm = Array.from(document.getElementById('passResetInputForm').children)
    var object = {};
    const findInputList = document.querySelectorAll('#findInputList input')
    findInputList.forEach(e => {
        e.addEventListener('input', data => {
            if (data.target.name == 'username') {
                object[data.target.name] = data.target.value;
            } else if (data.target.name == 'email') {
                object[data.target.name] = data.target.value;
            }
        })
    })

    const inputButtenResult = document.getElementById('findPassWordReset')
    const findPassWordCon = document.getElementById('findPassWordCon')
    findPassWordCon.addEventListener('click', () => {
        if ('username' in object && 'username' in object) {
            let url = '/api/find/pass'
            fetch(url, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(object)
            }).then(function (response) {
                if (!response.ok) {
                    response.json().then(res => {
                        return res
                    }).then(res => {
                        Object.keys(res).forEach(data => {
                            findInputList.forEach(value => {
                                if (data == value.name) {
                                    value.nextElementSibling.innerText = res[data]
                                }
                            })
                        })
                    })
                } else {
                    findInputList.forEach(e => e.value = '')
                    inputButtenResult.click()
                    const findPassWordResetMsg = document.getElementById("findPassWordResetMsg")
                    response.json().then(res => { return res }).then(res => {
                        findPassWordResetMsg.innerText = res['message']
                    })

                }
            });
        }
    })
}

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
        for (let i = starPointIcon.length; 0 <= i; i--) {
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


    function formInputNotCheck() {
        if (titleValueText.value == '' && ContentsValueText.value == '' && alcoholValueText.value == ''
            && rateCheckItem(acidityRate) == '' && rateCheckItem(bodyRate) == ''
            && rateCheckItem(sugarRate) == '' && priceValueText.value == '') {
            document.getElementById('postModalBtt').click()
            return false
        }
        return true
    }

    // 별 체크 레이팅
    const acidityRate = document.getElementsByName("acidityEat")
    const bodyRate = document.getElementsByName("bodyEat")
    const sugarRate = document.getElementsByName("sugarEat")

    function rateCheckItem(item) {
        let itemNum = ""
        item.forEach(e => { if (e.checked) itemNum = e.value })
        return itemNum
    }

    tastingBtnSumbit.addEventListener('click', () => {
        if (formInputNotCheck()) {
            let numberInfo = tastingPostCall()
            if (numberInfo != null)
                location.href = "/post/info/" + numberInfo + "/1"
        }
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
                document.getElementById('postModalBtt').click()
            } else if (response.url.includes('/login')) {
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

    tastingDeSumbit.addEventListener('click', () => {
        infoPostDeCall()
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


let postCon = ''
function postListWineTasting(page, id, link) {
    let url = '/api/v1/post/' + page + '/' + link + '/' + id
    fetch(url)
        .then(e => e.json())
        .then(json => {
            const tableBodyEl = document.getElementById("userTastingTableBody")
            const tableTrEl = document.createElement("tr")

            if (json.totalElements != 0) {
                removeAllchild(tableBodyEl)
                for (let i = 0; i < json.content.length; i++) {
                    let numberCount = ((json.totalElements - (json.pageable.pageNumber * 5)) - (i))
                    tableTrEl.innerHTML = `
                        <td>
                            <div class="row justify-content-md-center align-self-center">
                                <div class="row">
                                    <b class="col-md-2"><i class="fa-solid fa-book me-1"></i>`+ numberCount + `</b>
                                    <span class="col-md-6 font-bold mb-0">`+ (json.content[i].title.slice(0, 40)) + (json.content[i].title.length > 40 ? '...' : '') + `</span>
                                    <div class="col-md-4">
                                        <div class="row">
                                            <span class="col-7 mb-0">`+ json.content[i].userId + `</span>
                                            <span class="col-5 text-center mb-0">`+ json.content[i].modifieDate + `</span>
                                        </div>
                                     </div>
                                </div>
                            </div>
                        </td>
                    `
                    // 번호 등록
                    tableTrEl.setAttribute('data-columnNum', json.content[i].postId)
                    // 공동
                    tableBodyEl.appendChild(tableTrEl.cloneNode(true));
                }
                // 컨텐츠 이동
                tableBodyEl.querySelectorAll('tr[data-columnNum]').forEach(e => { // 공통
                    e.addEventListener('click', () => {
                        location.href = "/post/info/" + e.dataset.columnnum + "/" + (postCon == '' ? json.pageable.pageNumber + 1 : postCon)
                    })
                })
                pageNavDataSet('wineInfoPagelistNavBar', postListWineTasting, id, json, link)
            } else {
                createNoneMessageAllchild(document.getElementById("headTasting"), tableTrEl, tableBodyEl, `<div class="mt-4"><b>등록된 테이스팅 노트가 없습니다.</b></div>`)
            }

        });
}

// 마이 포스팅
function postListMyTasting(num) {
    let url = '/api/v1/account/post/wine/' + num
    fetch(url)
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
                <img src="`+ json.content[i].wineImageUrl + `" class="card-img-top" alt="...">
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
                        postListWineTasting(1, e.dataset.columnnum, 'wine')
                        document.getElementById('postCreatBtt').href = "/post/" + e.dataset.columnnum
                        postCon = (json.pageable.pageNumber + 1)
                    })
                })
                pageNavDataSet('tastingPageList', postListMyTasting, 0, json)
            } else {
                createNoneMessageAllchild(document.getElementById("topContentBody"), tastingDivCard, document.getElementById("testingMassage"), `<b>지금 바로 포스팅을 작성해보세요!.</b>`)
            }
        })
}

export { postListWineTasting, postListMyTasting }


// 코멘 정보
export function postCommentSubmit(id, coid, methods, data, commentInputId) {
    const commentInput = commentInputId
    if (commentInput.value.length >= 15) {
        let url = '/api/v1/comment'
        fetch(url, {
            method: methods,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                commentId: coid,
                comment: commentInput.value,
                postId: id
            }),
        }).then(function (response) {
            if (!response.ok) {
                document.getElementById('postModalBtt').click()
            } else if (response.url.includes('/login')) {
                location.href = "/account/login";
            } else {
                postCommentList(1, id, data)
                commentInput.value = ''
            }
        });
    } else {
        alert('15자리 이상 입력해주세요.')
    }
}

// 코멘 리스트
let commentDelId = ''
let commentEditId = ''
let commentPre = ''
let thisCommentCountNum = 1
export function postCommentList(page, item, data) {
    // 삭
    function infoCommentDeCall(num) {
        let url = '/api/v1/comment/' + num
        fetch(url, {
            method: 'DELETE',
        }).then(function (response) {
            if (!response.ok) {
                let error = response
                error.then(() => {
                })
            } else {
                con = true
                postCommentList(thisCommentCountNum, item, data)
            }
        });
    }
    // 버튼컨트롤
    function commitBttCreate(e) {
        e.parentElement.querySelectorAll('button').forEach(btt => {
            if (btt.classList.contains('d-none')) {
                btt.classList.remove('d-none')
            } else {
                btt.classList.add('d-none')
            }
        })
    }

    // 셀렉트 컨
    function selectForClick(body, selected, bool) {
        body.querySelectorAll(selected).forEach(e => {
            e.addEventListener('click', () => {
                if (bool) {
                    commentEditId.setAttribute('readonly', 'true')
                    commentEditId.classList.remove('border', 'border-2')
                    commentEditId.value = commentPre
                } else {
                    commentEditId.removeAttribute("readonly")
                    commentEditId.classList.add('border', 'border-2')
                }
                commitBttCreate(e)
            })
        })
    }
    let url = '/api/v1/comment/' + item + '/' + page
    fetch(url)
        .then(e => e.json())
        .then(json => {
            const tableBodyEl = document.getElementById("userTastingTableBody")
            const tableTrEl = document.createElement("tr")
            if (json.totalElements != 0) {
                removeAllchild(tableBodyEl)
                for (let i = 0; i < json.content.length; i++) {
                    let numberCount = ((json.totalElements - (json.pageable.pageNumber * 5)) - (i))
                    let editBtt = function () {
                        if (data == json.content[i].num) {
                            return `<div class="row-auto bg-white">
                                <div class="row justify-content-center">
                                    <button class="col-auto btn btn-sm btn-outline-secondary" data-button-type="edit"><i class="fa-solid fa-pen"></i>edit</button>
                                    <button class="col-auto btn btn-sm btn-outline-secondary d-none" data-button-type="modify"><i class="fa-solid fa-check"></i></i>modify</button>
                                    <button class="col-auto btn btn-sm btn-outline-secondary d-none" data-button-type="cancel"><i class="fa-solid fa-xmark"></i>cancel</button>
                                    <button class="col-auto btn btn-sm btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#delCommentModalCenter"><i class="fa-solid fa-eraser"></i>del</button>
                                </div>
                            </div>`
                        }
                        return ''
                    }
                    tableTrEl.innerHTML = `
                        <td class="p-0">
                            <div class="row-auto ps-3 py-2 border-bottom justify-content-center align-self-center" data-bs-toggle="collapse" data-bs-target="#collapseOne`+ i + `" aria-expanded="true" aria-controls="collapseOne` + i + `" id="heading` + i + `">
                                <div class="row">
                                    <b class="col-md-2"><i class="fa-solid fa-book me-1"></i>`+ numberCount + `</b>
                                    <span class="col-md-5 font-bold mb-0">`+ (json.content[i].comment.slice(0, 20)) + (json.content[i].comment.length > 20 ? '...' : '') + `</span>
                                    <div class="col-md-5">
                                        <div class="row">
                                            <span class="col-5 mb-0">`+ json.content[i].userId + `</span>
                                            <span class="col-7 text-center mb-0">`+ json.content[i].createDate + `</span>
                                        </div>
                                     </div>
                                </div>
                            </div>
                            <div id="collapseOne`+ i + `" class="accordion-collapse bg-white collapse" aria-labelledby="heading` + i + `" data-bs-parent="#accordion">
                                <div class="row-auto">
                                    <div class="col-md-12">
                                    <textarea class="comment outcontent px-3 py-2" data-button-type="textarea"
                                    placeholder="최소 15자리 이상 입력" rows="4" style="resize : none;" readonly>`+ json.content[i].comment + `</textarea>
                                    <span class="d-none" data-button-type="textareare">`+ json.content[i].comment + `</span>
                                    </div>
                                    `+ editBtt() + `
                                </div>
                            </div>
                        </td>
                    `
                    // 번호
                    tableTrEl.setAttribute("data-commentNum", json.content[i].commentId)
                    // 공동
                    tableBodyEl.appendChild(tableTrEl.cloneNode(true));
                }
                tableBodyEl.querySelectorAll('tr[data-commentNum]').forEach(e => {
                    e.addEventListener('click', () => {
                        commentDelId = e.dataset.commentnum
                        commentEditId = e.querySelector('textarea')
                        commentPre = e.querySelector('span[data-button-type="textareare"]').innerText
                    })
                })
                tableBodyEl.querySelectorAll('button[data-button-type="modify"]').forEach(e => {
                    e.addEventListener('click', () => {
                        postCommentSubmit(item, commentDelId, 'PUT', data, commentEditId)
                    })
                })
                selectForClick(tableBodyEl, 'button[data-button-type="edit"]', false) // edit
                selectForClick(tableBodyEl, 'button[data-button-type="cancel"]', true) // cancel
                const commentDel = document.getElementById("delCommentCheckBtt").addEventListener('click', () => {
                    if (con) {
                        con = false
                        infoCommentDeCall(commentDelId)
                    }
                })
                pageNavDataSet('wineInfoPagelistNavBar', postCommentList, item, json, data)
            } else {
                removeAllchild(document.getElementById('wineInfoPagelistNavBar'))
                removeAllchild(tableBodyEl)
                const newTable = document.createElement("div")
                newTable.setAttribute('class', 'mt-4')
                createNoneMessageAllchild(document.getElementById("headTasting"), newTable, tableBodyEl, `<b>등록된 댓글이 없습니다.</b>`)
            }

        });
}



//------------------------------------------------------------------------------------------------
export function mainLinkedList() {
    const mainBestPost = document.getElementById('mainBestGallery')

    mainBestPost.querySelectorAll('div[data-postNum]').forEach(e => {
        e.addEventListener('click', () => {
            location.href = '/post/info/' + e.dataset.postnum + '/1'
        })
    })

}

export function noticeLinks() {
    const mainNoticeList = document.getElementById('noticeBoardList')
    mainNoticeList.querySelectorAll('tr[data-noticeNum]').forEach(e => {
        e.addEventListener('click', () => {
            setCookie("_vi", 1)
            location.href = '/notice/' + e.dataset.noticenum
        })
    })
}


export function noticeFun(page) {

    noticeListWineTasting(page)

    function noticeListWineTasting(page) {
        let url = '/api/v1/notice/' + page
        fetch(url)
            .then(e => e.json())
            .then(json => {
                const tableBodyEl = document.getElementById("noticeBoardList")
                const tableTrEl = document.createElement("tr")

                if (json.totalElements != 0) {
                    removeAllchild(tableBodyEl)
                    for (let i = 0; i < json.content.length; i++) {
                        let numberCount = ((json.totalElements - (json.pageable.pageNumber * 10)) - (i))
                        tableTrEl.innerHTML = `
                            <td data->
                                <div class="row justify-content-md-center align-self-center">
                                    <b class="col-md-2"><i class="fa-solid fa-book me-1"></i>`+ numberCount + `</b>
                                    <span class="col-md-8 font-bold mb-0">`+ (json.content[i].noticeTitle.slice(0, 40)) + (json.content[i].noticeTitle.length > 40 ? '...' : '') + `</span>
                                    <span class="col-auto text-center mb-0">`+ json.content[i].modifieDate + `</span>
                                </div>
                            </td>
                        `
                        // 번호 등록
                        tableTrEl.setAttribute('data-noticenum', json.content[i].noticeId)
                        // 공동
                        tableBodyEl.appendChild(tableTrEl.cloneNode(true));
                    }
                    // 컨텐츠 이동
                    tableBodyEl.querySelectorAll('tr[data-noticenum]').forEach(e => { // 공통
                        e.addEventListener('click', () => {
                            location.href = '/notice/' + e.dataset.noticenum
                            setCookie("_vi", json.pageable.pageNumber+1)
                        })
                    })
                    pageNavDataSet('noticePagelistNavBar', noticeListWineTasting, 0, json)
                } else {
                    createNoneMessageAllchild(document.getElementById("headTasting"), tableTrEl, tableBodyEl, `<div class="mt-4"><b>등록된 테이스팅 노트가 없습니다.</b></div>`)
                }

            });
    }
}

export function noticePostSet(){
    const Editor = toastui.Editor;
            const editor = new Editor({
                el: document.querySelector('#editor'),
                height: '500px',
                initialEditType: 'wysiwyg',
                previewStyle: 'vertical',
                hooks: {
                    addImageBlobHook: (blob, callback) => {
                        const upload = blob
                        noticePhotoUpload(blob).then(e=> { callback(e,"alt")
                        console.log(e.url)})
                    }
                }
            });
            async function noticePhotoUpload(blob) {
                let url = '/api/v1/notice/image/upload'
                const formData = new FormData();
                formData.append("multipartFiles", blob);
                return await fetch(url, {
                    method: 'POST',
                    body: formData
                }).then(function (response) {
                    if (response.ok) {
                        return response.text()
                    } else if (response.url.includes('/login')) {
                        location.href = "/account/login";
                    }
                });
            }

            document.getElementById('noticePostSubmit').addEventListener('click', () => {
                const title = document.getElementById('noticeTitle')
                let url = '/api/v1/notice/post'
                fetch(url, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        noticeTitle : title.value,
                        noticeContents : editor.getHTML()
                    })
                }).then(function (response) {
                    if (response.ok) {
                        response.text().then(e=>{
                            location.href = "/notice/" + e
                        })
                    } else if (response.url.includes('/login')) {
                        location.href = "/account/login";
                    }
                });
            })
}

