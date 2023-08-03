package org.zerock.boardex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    @Builder.Default
    private int page=1;     //페이지 정보(1page)
    @Builder.Default
    private int size=10;   //페이지 크기
    private String type;   //검색 유형(t,c,w)
    private String keyword;    //검색 키워드
    //type이라는 문자열을 배열로 반환함.
    public String[] getTypes(){
        //type값이 null이거나 비어있으면 null 반환함.
        if(type==null || type.isEmpty()){
            return null;
        }
        //type값이 있으면 공백을 기준으로 문자열을 나누어서 배열로 반환, 예:type="자바 함수"=>type[0]=자바, type[1]=함수
        return type.split("");
    }
    //페이지 처리 메서드
    //String...props : 정렬 기준 필드(bno:글번호)들을 나타냄
    public Pageable getPageable(String...props){
        //0번부터 시작하는 페이지 번호와 개수(size), 정렬 기준 필드들(props), 정렬방향(descending)
        return PageRequest.of(this.page -1, this.size, Sort.by(props).descending());
    }
    //검색조건과 페이징 조건 등을 문자열로 구성하는 메서드
    private String link;
    public String getLink(){
        if(link==null){
            //StringBuilder : String객체를 만들 때 기존의 문자열에 데이터를 더하는 방식, 속도 빠르고 부하가 적음.
            StringBuilder builder=new StringBuilder();
            //builder = "page=3&size=10&type=tcw&keyword=자바"
            builder.append("page="+this.page);
            builder.append("&size="+this.size);
            //type변수에 데이터가 있으면(null이 아니면서 길이가 0보다 크면)
            if(type != null && type.length() > 0){
                builder.append("&type="+type);
            }
            //keyword 가 비어 있지 않으면
            if(keyword != null){
                //에러가 없으면 try다음의 메서드 실행하고, 에러가 있으면 예외처리
                try {
                    builder.append("&keyword="+ URLEncoder.encode(keyword,"UTF-8"));
                } catch (UnsupportedEncodingException e) {}
            }
            link = builder.toString();
        }
        return link;
    }
}
