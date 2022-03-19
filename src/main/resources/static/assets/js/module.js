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
            querys.addEventListener('keypress', function () {
                if (querys.value.length > 0) {
                    queryInput.value = querys.value;
                    queryInput.removeAttribute('disabled')
                } else {
                    queryInput.value = "";
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
    const searchCheck = function searchChecked(id, value) {
        children.forEach(child => {
            if (child.id == id) {
                child.value = value;
                child.removeAttribute("disabled")
            }
        })
    }


    // 타입 추론
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
                if (type.value == "displayName")
                    valueLabel = "이름"
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
            if (e.name != 'type' && e.name != 'attr' && e.name != 'page') {
                e.value = "";
                e.setAttribute('disabled', 'disabled')
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

function itemDivIndexController(){

    return {
    }
}

export { itemDivIndexController }