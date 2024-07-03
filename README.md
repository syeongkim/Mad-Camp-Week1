# 자니..?
익숙함에 속아 소중함을 잃지 않도록 


## Outline
![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/378b4e5a-2782-447f-9543-5ca26acaee72)

**자니..?**는 소중한 인연들을 잃지 않도록 주기적으로 연락을 리마인드 해주는 앱입니다.

소중한 인연의 연락처 관리, 소중한 인연과의 추억 관리, 소중한 인연과의 연락 관리를 할 수 있는 3개의 탭으로 구성되어 있습니다.


## Team
- [김서영](https://github.com/syeongkim) 
- [박영민](https://github.com/YoungMin0B)


## Tech Stack

**Front-end** : Kotlin

**IDE** : Android Studio


## About


**Intro&Tablelayout**

- 앱을 처음 시작할 때 로고가 짜-앏게 떠오릅니다( 두-둥 이펙트).
    - splash를
- `Bottom Navigation Bar` 를 통해 각각의 탭으로 이동할 수 있습니다.

**(첨부)**
*Intro*

**Contact**

- 연락처 목록을 `RecyclerView` 로 제공합니다.
- 추가, 수정된 연락처를 저장하기 위해 `sharedpreference`를 사용했습니다.
    - 변경사항은 `SharedPreferences`에 업데이트된 `updatedContacts`를 저장하고, `RecyclerView`를 갱신함으로써 화면에 유지됩니다.
- 검색 기능
    - 돋보기 아이콘을 누르면 이름으로 연락처를 검색할 수 있습니다.
- 정렬 기능
    - 가나다 정렬: 가나다 순 버튼을 누르면 이름이 가나다 순으로 연락처가 정렬됩니다.
    - 추천 정렬: 추천 순 버튼을 누르면 사용자가 마음속으로 다시 만나고 싶어하는 사람을 알고리즘을 통해 분석해 연락처가 우선순위정렬됩니다.(내 마음을 알고 싶을 때 심심풀이 땅콩)흠-칟

- 연락처 추가 기능
    - 상단 연락처 추가 버튼을 누르면 연락처를 추가할 수 있습니다.
    - 이름, 전화번호, 생일 정보를 입력합니다.
    
- 연락처 열람 및 수정 기능
    - 개별 연락처 아이콘을 터치하여 생성되는 `contactdialog`로 상세정보를 조회할 수 있고, 수정 버튼과 저장 버튼을 통해 변경된 내용이 `sharedpreference`를 통해 저장됩니다.
    - 상세정보: 이름, 전화번호, 생일, 첫 인사 날짜, 마지막 인사 날짜, 최근 통화 기록
- 삭제 기능
    - 연락처 아이템을 길게 누르면 떠오르는 `alertdialog` 로 삭제 여부를 선택할 수 있습니다.

**(첨부)**
*Contact기능*

**Memory**

- 갤러리의 추억 사진들을 `GridView` 로 제공합니다.
- 추가, 수정된 추억 이야기를 저장하기 위해 `sharedpreference`를 사용했습니다.
    - 변경사항은 `SharedPreferences`에 업데이트된을 `ImageData`를 저장하고, `GridView`를 갱신함으로써 화면에 유지됩니다.

- 추억 추가 기능
    - 추억 추가 버튼을 통해 갤러리로 접근하여 선택한 이미지를 추가하고 관련된 추억정보를 입력할 수 있습니다.
    - 추억 정보: 함께한 사람, 생성 날짜, 추억 코멘트

- 추억 열람 및 수정 기능
    - 개별 이미지를 터치하여 생성되는 `dialog`로 상세정보를 조회할 수 있고, 수정 버튼과 저장 버튼을 통해 변경된 내용이 `sharedpreference`를 통해 저장됩니다.추억 추가 버튼을 통해 갤러리로 접근하여 선택한 이미지를 추가하고 관련된 추억 정보를 입력할 수 있습니다.
    - 추억 정보: 함께한 사람, 생성 날짜, 추억 코멘트
    
- 삭제 기능
    - 이미지를 길게 누르면 떠오르는 `alertdialog` 로 삭제 여부를 선택할 수 있습니다.

**(첨부)**
*Memory기능*

**Message**

- 소중한 추억들을 잊지 않도록 주기적으로 **연락을 리마인드하여** 소중한 인연과의 **연락 관리 서비스를 제공합니다.**

- 날짜 선택 기능을 통해 사용자는 특정 날짜를 선택하고, 해당 날짜에 대한 추억이나 생일, 연락처 정보를 확인할 수 있습니다.
    - 사용자가 특정 날짜를 선택하면, 해당 날짜와 연관된 추억이나 생일 등의 정보를 `TextView`와 `ImageView`를 통해 표시합니다.
- `AlertDialog`를 사용하여 추억 이미지와 세부 정보를 팝업 형태로 보여줍니다.
    - 추억 이미지의 경우, 앱이 너무 큰 비트맵 이미지를 처리하지 않도록 이미지를 리사이즈하여 메모리 사용을 최적화합니다.

- 연락 추천 알고리즘
    - 1년전 추억을 함께한 ,
    - 날짜기반 나이계산 생일인 ,
    - 최근 통화기록 기
    - 랜덤 추천 무작위로 연락처를
        - 랜덤 추천 시, 앱이 너무 큰 비트맵 이미지를 처리하지 않도록 이미지를 리사이즈하여 메모리 사용을 최적화합니다.

- 추천 해당 연락처의 전화번호로 전화 ,사용자가 수정 가능한 적절한 코멘트 제안하여 메시지를 전송

**(첨부)**
*Message기능*
