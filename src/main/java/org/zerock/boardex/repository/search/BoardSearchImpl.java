package org.zerock.boardex.repository.search;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.boardex.domain.Board;
import org.zerock.boardex.domain.QBoard;
import org.zerock.boardex.domain.QReply;
import org.zerock.boardex.dto.*;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch {
    public BoardSearchImpl (){
        super(Board.class);
    }
    @Override
    public Page<Board> search1 (Pageable pageable) {
        QBoard board=QBoard.board;
        JPQLQuery<Board> query=from(board);
        //title(제목)에 문자 1이 포함되는 데이터 검색
        query.where(board.title.contains("1"));
        //paging
        this.getQuerydsl().applyPagination(pageable,query);
        //쿼리문을 실행, fetch() : board에서 정보 조회
        List<Board> list=query.fetch();
        //쿼리문을 실행 : 조회한 정보의 개수
        long count=query.fetchCount();
        return null;
    }
    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable){
        QBoard board=QBoard.board;
        JPQLQuery<Board> query=from(board);
        //검색조건과 키워드가 있다면
        if((types != null && types.length > 0) && keyword != null ){
            //쿼리문 작성할 때 where문 구성하는 객체
            BooleanBuilder booleanBuilder=new BooleanBuilder();
            for(String type:types){
                switch (type){
                    case "t":
                        //게시판의 게시글 제목에 keyword가 포함되어 있는지 검색
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        //게시판의 게시글 내용에 keyword가 포함되어 있는지 검색
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        //게시판의 게시글 글쓴이에 keyword가 포함되어 있는지 검색
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            //위의 조건을 쿼리문에 적용하기
            query.where(booleanBuilder);
        }
        //글번호가 0보다 큰 데이터 검색
        query.where(board.bno.gt(0L));
        this.getQuerydsl().applyPagination(pageable, query);
        List<Board> list=query.fetch();
        long count=query.fetchCount();
        return new PageImpl<>(list, pageable, count);
        //list:실제 목록 데이터, pageable : 페이지 객체
        // count: 전체 개수
    }
    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable){
        //Board의 속성을 이용하여 다양한 SQL 구문을 처리할 수 있는 객체임.
        QBoard board=QBoard.board;
        QReply reply=QReply.reply;
        //일반 쿼리(Sql)은 data입장에서 실행
        //JPQLQuery 는 자바 입장에서 실행
        JPQLQuery<Board> query=from(board);
        //reply랑 board랑 leftOuterJoin 실행
        //게시글에 댓글이 없을 수도 있음.
        query.leftJoin(reply).on(reply.board.eq(board));
        //board를 기준으로 그룹 지정
        query.groupBy(board);
        //검색 조건과 검색어가 있다면
        if((types!=null && types.length>0) && keyword != null){
            //booleanBuilder객체 생성
            BooleanBuilder booleanBuilder=new BooleanBuilder();
            //types개수만큼 반복
            for(String type:types){
                //type이 t인지 c인지 w인지에 따라서 검색
                switch(type) {
                    case "t":
                        //게시판의 게시글 제목에 keyword가 포함되어 있는지 검색
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        //게시판의 게시글 내용에 keyword가 포함되어 있는지 검색
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        //게시판의 게시글 작성자에 keyword가 포함되어 있는지 검색
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }//end for
            query.where(booleanBuilder);
        }
        //게시글 번호가 0보다 큰 데이터 조회
        query.where(board.bno.gt(0L));

        JPQLQuery<BoardListReplyCountDTO> dtoQuery=query.select(
                //setter기반으로 반드시 DTO에 Setter가 있어야 함, 조회할 때 어떤 것을 기반으로 조회할 것인지를 설정하는 메서드임.
                Projections.bean(BoardListReplyCountDTO.class,
                board.bno,
                board.title,
                board.writer,
                board.regDate,
                reply.count().as("replyCount")
        ));
        //스프링 데이터가 제공하는 페이징을 Querydsl로 변환 가능
        //dtoQuery(게시글에서 번호,제목,내용,작성자,댓글갯수를 조회한 결과)의 내용을 페이징하기 쉽게 applyPagination메서드를 사용함.
        this.getQuerydsl().applyPagination(pageable, dtoQuery);
        //dtoQuery(조회결과)를 배열객체로 dtoList에 저장함.
        List<BoardListReplyCountDTO> dtoList=dtoQuery.fetch();
        //dtoQuery(조회결과) 개수를 count에 저장
        long count = dtoQuery.fetchCount();
        return new PageImpl<>(dtoList, pageable, count);
    }
    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable){
        //Querydsl 이용한 동적 SQL의 처리
        QBoard board=QBoard.board;
        QReply reply=QReply.reply;
        //게시판 객체에 자바 친화적인 질의어를 실행할 수 있도록 설정
        JPQLQuery<Board> boardJPQLQuery=from(board);
        //reply객체랑 board객체를 left outer join
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board));

        //검색 조건과 키워드 사용
        //types가 null이 아니고 길이가 0보다 크고, keyword값이 null이 아니라면
        if((types != null && types.length >0) && keyword != null){
            //where 뒤의 조건을 쿼리로 만들어주는 객체
            BooleanBuilder booleanBuilder=new BooleanBuilder();
            //types개수만큼 반복(3번 반복)
            for(String type:types){
                switch(type){
                    case "t":
                        //글제목에 keyword가 포함되어 있는 데이터 검색, (where title='keyword')
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        //글내용에 keyword가 포함되어 있는 데이터 검색, (where content='keyword')
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        //작성자에 keyword가 포함되어 있는 데이터 검색, (where writer='keyword')
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }
            //where 조건에 해당하는 데이터 조회하라는 쿼리 실행
            boardJPQLQuery.where(booleanBuilder);
        }

        //게시판을 기준으로 그룹 설정
        boardJPQLQuery.groupBy(board);

        //left outer join한 결과에 paging 추가
        getQuerydsl().applyPagination(pageable, boardJPQLQuery);
        //Tuple(행) : 각각 다른 타입의 데이터를 추출할 때 사용
        //countDistinct() : 중복제거한 값들의 개수
        JPQLQuery<Tuple> tupleJPQLQuery=boardJPQLQuery.select(board,reply.countDistinct());


        //left outer join한 결과 + paging 결과 값을 boardList에 배열로 저장
        //List<Board> boardList=boardJPQLQuery.fetch();

        List<Tuple> tupleList=tupleJPQLQuery.fetch();
        List<BoardListAllDTO> dtoList=tupleList.stream().map(tuple->{
            Board board1=(Board) tuple.get(board);
            long replyCount=tuple.get(1, Long.class);
            BoardListAllDTO dto=BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //BoardImage를 BoardImageDTO로 처리함
            List<BoardImageDTO> imageDTOS=board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setBoardImages(imageDTOS);
            return dto;

        }).collect(Collectors.toList());
        long totalCount=boardJPQLQuery.fetchCount();
        return new PageImpl<>(dtoList, pageable, totalCount);
    }
}
