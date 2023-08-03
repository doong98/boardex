//get1함수 선언, bno:매개변수, async : 비동기식
async function get1(bno){
    //상수형 변수 result선언, 비동기식으로 데이터 가져오기
    const result=await axios.get(`/replies/list/${bno}`);
    console.log(result);
}
//결과 데이터는 dtoList로 화면에 목록 보여주기
//댓글 목록 출력하는 함수 선언
//bno: 현재 게시물 번호, page: 페이지번호, size:페이지당 사이즈, goLast: 마지막 페이지 호출 여부를 매개변수로 전달받음
async function getList({bno, page, size, goLast}){
    //게시글 번호, 페이지 번호, 페이지 크기를 비동기식으로 데이터 가져와서 result에 저장
    const result=await axios.get(`/replies/list/${bno}`, {params:{page, size}});

    //만약 goLast값이 true이면(마지막 페이지라는 뜻)
    if(goLast){
        //전체 댓글 수
        const total=result.data.total;
        //전체 댓글 수를 페이지크기(size)로 나눈 값을 정수형으로 변환하여 lastPage변수에 저장
        //parseInt() : 실수형을 정수형으로 형변환
        const lastPage=parseInt(Math.ceil(total/size));
        //이 함수를 호출한 곳으로 결과값 반환
        return getList({bno:bno, page:lastPage, size:size});
    }
    return result.data;
}
//댓글 추가하기(post방식으로 데이터를 서버에 전달)
async function addReply(replyObj){
    const response=await axios.post(`/replies/`, replyObj);
    return response.data;
}
//댓글 조회하고 수정하기(GET방식)
async function getReply(rno) {
    const response=await axios.get(`/replies/${rno}`);
    return response.data;
}
async function modifyReply(replyObj){
    const response=await axios.put(`/replies/${replyObj.rno}`, replyObj);
    return response.data;
}
async function removeReply(rno){
    const response=await axios.delete(`/replies/${rno}`);
    return response.data;
}