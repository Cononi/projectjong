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

function tastingPostSubmitContents() {

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
    function totalPointControll() {
        rangeValueBtt.addEventListener('input', e => (
            rangeValueText.innerText = e.target.value + " / 100"
        ))
        rangeValueBtt.addEventListener('input', (e) => {
            let rangeV = e.target.value / 20
            for (i = starPointIcon.length; 0 <= i; i--) {
                if (Math.floor(rangeV) > i)
                    starPointIcon[i].classList = "bi bi-star-fill fa-2x"
                else if (rangeV >= i + 0.5)
                    starPointIcon[i].classList = "bi bi-star-half fa-2x"
                else if (i != 0)
                    starPointIcon[i - 1].classList = "bi bi-star fa-2x"
            }
        })
    }

    // 별 체크 레이팅
    const acidityRate = document.getElementsByName("acidityEat")
    const bodyRate = document.getElementsByName("bodyEat")
    const sugarRate = document.getElementsByName("sugarEat")
    let acidityRateVal = ""
    let bodyRateVal = ""
    let sugaRateVal = ""
    function tastingRateStarPoint() {
        acidityRate.forEach(e => e.addEventListener('click', f => {
            acidityRateVal = f.target.value
        }))
        bodyRate.forEach(e => e.addEventListener('click', f => {
            bodyRateVal = f.target.value
        }))
        sugarRate.forEach(e => e.addEventListener('click', f => {
            sugaRateVal = f.target.value
        }))
    }
    tastingBtnSumbit.addEventListener('click', async () => {
        let numberInfo = await tastingPostCall()
        location.href = "/post/info/" + numberInfo
    })

    function tastingPostCall() {
        let url = '/api/v1/post'
        return fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                title: titleValueText.value,
                contents: ContentsValueText.value,
                vintage: vintageValueText.value,
                alcohol: alcoholValueText.value,
                acidityCount: acidityRateVal,
                bodyCount: bodyRateVal,
                sugarCount: sugaRateVal,
                score: rangeValueBtt.value,
                price: priceValueText.value,
                wineId: itemIdSet.dataset.columns
            }),
        }).then(function (response) {
            if (!response.ok) {
                let error = response
                error.then(e =>
                    console.log(e))
            } else
                return response.json()
        });
    }
    // 리턴
    tastingRateStarPoint()
    totalPointControll()

    return {
        totalPointControll: totalPointControll(),
        tastingRateStarPoint: tastingRateStarPoint()
    }
}

export { tastingPostSubmitContents }


export async function postListWineTasting(page, id) {
    let url = '/api/v1/post/' + page + '/list/' + id
    return await fetch(url)
        .then(e => e.json())
        .then(json => {
            console.log(json)
            const tableBodyEl = document.getElementById("userTastingTableBody")
            const tableTrEl = document.createElement("tr")
            for (i = 0; i < json.content.length; i++) {
                tableTrEl.innerHTML = `
                    <td data-columnNum="`+ json.content[i].postId + `" class="col-md-3">
                        <div class="d-flex align-items-center">
                            <div class="avatar avatar-md">
                                <img src="`+ json.content[i].userProfileImgUrl + `">
                            </div>
                            <p class="font-bold ms-3 mb-0">`+ json.content[i].userId + `</p>
                        </div>
                    </td>
                    <td class="col-md-9">
                        <p class=" mb-0">`+ json.content[i].title + `</p>
                    </td>
                `
                tableBodyEl.appendChild(tableTrEl.cloneNode(true));
            }
            const tableNav = document.getElementById("wineInfoPagelistNavBar")
            const tableNavUl = document.createElement("ul")
            tableNavUl.setAttribute('class', 'pagination pagination-danger justify-content-center')

            let firstpage = ((json.pageable.pageNumber / 5) * 5+1)
            if(firstpage > 5){
                tableNavUl.innerHTML = `
                <li class="page-item"><a class="page-link" data-columnNum=`+ firstpage +`><span aria-hidden="true"><i
                            class="bi bi-chevron-left"></i></span></a></li>
                `
            }
            for (i = 0; i < json.totalPages; i++) {
                tableNavUl.innerHTML += `
                <li class="page-item"><a class="page-link">1</a></li>
                `
            }

            tableNavUl.innerHTML += `
            <li class="page-item">
            <a class="page-link">
                <span aria-hidden="true">
                    <i class="bi bi-chevron-right"></i></span></a>
            </li>
            `
            tableNav.appendChild(tableNavUl)
        });
}


