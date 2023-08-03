package org.zerock.boardex.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
//<T> : Type parameter(generic type:제네릭 타입) "1"=>1
public class PageResponseDTO<E> {
    private int page;
    private int size;
    private int total;    //전체 페이지 수
    private int start;   //시작 페이지 번호
    private int end;    //끝 페이지 번호
    private boolean prev;   //이전 페이지 존재 여부
    private boolean next;   //다음 페이지 존재 여부
    private List<E> dtoList;    //전체 목록(리스트)

    //builderMethodName="withAll" : withAll():PageResponseDTOBuiler<E> 라는 이름을 가진 메서드 생성
    @Builder(builderMethodName="withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {
        //검색결과가 없으면 반환값 없음.
        if(total <= 0){
            return;
        }
        //pageRequestDTO객체의 getPage() 메서드 호출
        this.page=pageRequestDTO.getPage();
        //pateRequestDTO객체의 getSize() 메서드 호출
        this.size=pageRequestDTO.getSize();
        //전체 목록 수
        this.total=total;
        //전체 목록(리스트)
        this.dtoList=dtoList;
        //화면에서의 마지막 번호, Math.ceil() : 실수를 정수로 만듬(올림)
        //this.end=>(int)(Math.ceil(1/10.0))*10
        //this.end=>(int)(Math.ceil(0.1))*10=>(int)1.0*10=(int)10.0=>10
        this.end=(int)(Math.ceil(this.page / 10.0)) * 10;
        //화면에서의 시작 번호
        this.start=this.end - 9;
        //데이터의 개수를 계산한 마지막 페이지 번호
        //(int)(Math.ceil((172/10))=>(int)(Math.ceil(17.2))=>(int)18.0=> 18
        int last=(int)(Math.ceil((total/(double)size)));
        // 조건 ? true : false => 삼항 연산자
        this.end=end > last ? last : end;
        //시작 페이지 번호가 1보다 크면 true, 작으면 false
        //true:이전 페이지 있음, false:이전 페이지 없음.
        this.prev=this.start > 1;
        //마지막 페이지 번호가 end*size보다 크면 true,작으면 false-> true:다음 페이지 있음, false:다음 페이지 없음.
        this.next=total > this.end * this.size;
    }
}
