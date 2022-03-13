function SearchInfoController() {
    const searchMainForm = document.getElementById("searchForms")
    const children = Array.from(searchMainForm.children)
    children.forEach(child => {
        if(!child.value) {
            if(child.name != "query")
            child.setAttribute("disabled","disabled")
        }
    })
    
    // 검색창 체크
    function searchInputText() {
        const result = document.querySelectorAll("input[name=queryname]").forEach(e =>
            e.addEventListener('keypress', function () {
                document.getElementById("query").value = e.value;
                console.log(e.value)
            }))
        return result;
    }
    // 검색창
    function submitForm() {
        const searchForm = document.querySelectorAll("form.input-group")
        searchForm.forEach(e => e.addEventListener('submit', e => {
            e.preventDefault()
            searchMainForm.submit()
        }))
    }

    // 상세 검색 속성 체크
    function searchConditonCheck() {
        const searchDetails = document.querySelectorAll("input~label")
        // let searchChecked = document.querySelectorAll("input[checked]")
        // searchChecked.forEach(check => {
        //     searchCheck(check.name,check.value)
        // })
        searchDetails.forEach(details => details.addEventListener('click', function () {
            details.previousElementSibling.setAttribute('checked',false)
            searchCheck(details.previousElementSibling.name, details.previousElementSibling.value)
        }));
    }

    // 나라 리스트.



    /*
        내부 변수 모집
    */
    // 체크 인풋
    const searchCheck = function searchChecked(id, value) {
        const children = Array.from(searchMainForm.children)
        children.forEach(child => {
            if (child.id == id) {
                child.value = value;
                child.removeAttribute("disabled")
            }
        })
    }




    return {
        input: searchInputText(),
        submit: submitForm(),
        check: searchConditonCheck()
    }
}



export { SearchInfoController}


