// pageNavDataSet(MainELId, customerData, ItemPageId, json, link) { //MainElDataSet (테이블 데이터 셋), MainElId (메인 El Id), MainEl (메인 El), customerData (객체 전달),ItemPageId(페이지 아이템 관련)  json(Json데이터)
import { setCookie, createNoneMessageAllchild, removeAllchild, pageNavDataSet} from '/js/common.js';


function fetchUsers(page) {
    let url = '/api/admin/users/' + page
    return fetch(url).then(function (response) {
        return response.json();
    });
}
function adminUsersPage(page) {
    usersPage(1)

    async function usersPage(page) {
        let json = await fetchUsers(page)
        const tastingDivEl = document.getElementById("mytastingDiv")
        const tastingDivCard = document.createElement("div")

        console.log(json)

        if (json.totalElements != 0) {
            tastingDivCard.setAttribute("class", "card box-shadow mx-1 my-3 text-center users-card-content")

            removeAllchild(tastingDivEl)
            for (let i = 0; i < json.numberOfElements; i++) {
                tastingDivCard.innerHTML = `
                <div class="avatar avatar-lg justify-content-center mt-3">
                    <img src="`+ (json.content[i].profileImageUrl == null ? '/assets/images/faces/default.jpg' : json.content[i].profileImageUrl) + `" alt="mdo" class="card-img-top">
                </div>
                <div class="users-card">
                    <div class="row text-start mt-3">
                        <span class="fw-bold text-primary"><i class="fa-solid fa-wand-magic-sparkles"></i> `+ json.content[i].roles + `</span>
                        <span><i class="fa-solid fa-user"></i> `+ json.content[i].username + `</span>
                        <span><i class="fa-solid fa-square-check"></i> `+ json.content[i].name + `</span>
                        <span class="`+ (json.content[i].isActive == true ? "text-success" : "text-danger") +`"><i class="fa-solid fa-lock red"></i> `+ (json.content[i].isActive == true ? "활성 계정" : "차단 계정") + `</span>
                        <span class="`+ (json.content[i].isEmailEnabled == true ? "text-success" : "text-danger") +`"><i class="fa-solid fa-at"></i> `+ (json.content[i].isEmailEnabled == true ? "인증 계정" : "비인증 계정") + `</span>
                    </div>
                </div>
            `
                // 번호
                tastingDivCard.setAttribute("data-columnNum", json.content[i].id)
                //  공동
                tastingDivEl.appendChild(tastingDivCard.cloneNode(true));
            }
            // 컨텐츠 이동
            tastingDivEl.querySelectorAll('div[data-columnNum]').forEach(e => { // 공통
                e.addEventListener('click', () => {
                    location.href = "/admin/users/" + e.dataset.columnnum
                })
            })
            pageNavDataSet('PageList', usersPage, 0, json)
        }
    }

}



export {adminUsersPage}