//서버에 파일 업로드하는 메서드 생성
async function uploadToServer(formObj){
    console.log("서버에 업로드");
    console.log(formObj);

    const response=await axios({
        method:'post',
        url:'/upload',
        data:formObj,
        headers: {
            'Content-Type':'multipart/form-data',
        }
    });
    return response.data;
}
//첨부파일 삭제하는 메서드 생성
async function removeFileToServer(uuid, fileName){
    const response=await axios.delete(`/remove/${uuid}_${fileName}`)
    return response.data
}