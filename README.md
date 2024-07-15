## 자니..?
---
익숙함에 속아 소중함을 잃지 않도록 (~~자니..? 우리 그때 행복했잖아… 기억나..?~~),

연락 리마인드 앱 **자니..?** 로 소중한 인연을 관리하세요! ✉️



### Outline
---
![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/1aecfd4b-5818-44f1-97b3-0de1096803b3)



 **자니..?** 는 소중한 인연들을 잃지 않도록 주기적으로 **연락을 리마인드** 해주는 앱입니다.

소중한 인연의 **연락처 관리**, 소중한 인연과의 **추억 관리**, 소중한 인연과의 **연락 관리**를 할 수 있는 3개의 탭으로 구성되어 있습니다.


### Team
---
- #### 김서영
- #### 박영민



### Tech Stack
---
**Front-end** : Kotlin

**IDE** : Android Studio



### About
---
📱 **Intro & Tablayout** 

- `Splash`를 사용하여 앱 로딩 화면을 제작했습니다.
- `Bottom Navigation Bar` 를 통해 각각의 탭으로 이동할 수 있습니다.


📞 **Contact**

- 연락처 확인
    - 저장된 연락처 목록을 `RecyclerView` 로 제공합니다.
    - 추가, 수정된 연락처를 저장하기 위해 `sharedpreference`를 사용했습니다.
        - 변경사항은 `SharedPreferences`에 업데이트된 `updatedContacts`를 저장하고, `RecyclerView`를 갱신함으로써 화면에 유지됩니다.
- 저장된 이름으로 연락처 검색
    - 돋보기 아이콘을 누르면 이름으로 연락처를 검색할 수 있습니다.
- 연락처 정렬 기능
    - 가나다 정렬: 가나다 순으로 연락처를 정렬합니다.
    - 추천 정렬: 알고리즘에 의해 사용자가 놓치기 쉬운 인연 순으로 연락처를 정렬합니다. (내 마음을 알고 싶을 때 심심풀이 땅콩)
- 연락처 추가 기능
    - 상단의 연락처 추가 버튼을 누르면 새로운 연락처를 추가할 수 있습니다.
    - 이름, 전화번호, 생일 정보를 입력합니다.
- 연락처 열람 및 수정 기능
    - 개별 연락처 아이콘을 터치하여 생성되는 `contactdialog`로 연락처에 대한 상세정보를 조회할 수 있고, 수정 버튼과 저장 버튼을 통해 변경된 내용이 `sharedpreference`를 통해 저장됩니다.
    * 상세정보: 이름, 전화번호, 생일, 첫 인사 날짜, 마지막 인사 날짜, 최근 통화 기록
- 삭제 기능
    - 연락처 아이템을 길게 누르면 나오는 `alertdialog` 로 연락처를 삭제할 수 있습니다.


📸 **Memory** 

- 추억 확인
    - 갤러리의 추억 사진들을 `GridView` 로 확인할 수 있습니다.
    - 수정 또는 추가된 추억 이야기를 `sharedpreference`를 사용하여 저장합니다.
    - 변경사항은 `SharedPreferences`에 업데이트된 `ImageData`를 저장하고, `GridView`를 갱신함으로써 화면에 유지됩니다.
- 추억 추가 기능
    - 추억 추가 버튼을 통해 갤러리로 접근하여 이미지를 선택하여 관련된 추억정보를 입력할 수 있습니다.
    * 추억 정보: 함께한 사람, 추억 생성 날짜, 추억에 대한 코멘트
- 추억 열람 및 수정 기능
    - 개별 이미지를 터치하여 생성되는 `dialog`로 상세정보를 조회할 수 있고, 상세 정보를 수정할 수 있습니다. 변경된 내용은 `sharedpreference`를 통해 저장됩니다. 
    * 추억 정보: 함께한 사람, 추억 생성 날짜, 추억에 대한 코멘트
- 삭제 기능
    - 이미지를 길게 누르면 나오는`alertdialog` 로 추억을 삭제할 수 있습니다.


💬 **Message** 

소중한 추억들을 잊지 않도록 주기적으로 **연락을 리마인드하여** 소중한 인연과의 **연락을 관리할 수 있는 서비스를 제공합니다.**

- 연락 추천 기능
    - 오늘 연락하면 좋을 사람과 연락할 메세지를 제공합니다.
- 날짜 변경 기능
    - 날짜를 변경하여 날짜에 따른 추천을 받을 수 있습니다.
- 이미지를 클릭하면 연락에 대한 세부 정보를 확인할 수 있습니다.
    - 추억에 기반한 연락 추천의 경우, 추억에 대한 세부 정보를 확인할 수 있습니다.
    * 세부 정보: 함께한 사람, 추억 생성 날짜, 추억에 대한 코멘트
    - 연락 기록에 기반한 연락 추천의 경우, 그 사람에 대한 세부 정보를 확인할 수 있습니다.
    * 세부 정보: 이름, 연락처, 생일, 연락처 저장 날짜, 마지막 연락 날짜
- 연락 추천 알고리즘
    - n년 전 추억이 있는 경우, 추억을 함께한 사람에게 연락 추천
    - 오늘 생일인 사람이 있는 경우, 생일인 사람에게 연락 추천
    - 2년 이상 연락하지 않은 사람들 중, 랜덤으로 연락 추천
    - 모든 연락처 중 랜덤으로 연락 추천
- 연락 연결 기능
    - 전화 걸기 버튼을 클릭하여 추천 받은 연락처로 바로 전화를 걸 수 있습니다.
    - 문자 보내기 버튼을 클릭하며 추천 받은 연락처로 바로 문자를 보낼 수 있습니다.
    **



### Lessons Learned
---
<aside>
❓ emulator에서는 잘 실행되는 앱이 공기계에서는 실행되지 않고, 또 공기계 기종에 따라 앱이 종료되는 시점이 다른 오류가 있었는데, 원인은 이미지 파일의 용량이 커서 로딩되는 시간이 너무 오래 걸렸기 때문임

</aside>
<br>
<aside>
❗ 기능 개발 뿐만 아니라 프로젝트 최적화에도 많은 노력을 기울여야 함을 깨달았음.

</aside>



### Preview
---
📱 **Intro & Tablayout**


<img src="https://github.com/user-attachments/assets/7d296e64-576a-4128-86a4-c889e96d62f2">


📞 **Contact**


![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/0db7babc-e9d3-4b60-8276-1f7048915d3d)
![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/a3251003-5824-44fd-8ab8-489496c597f4)
![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/4c1d3a2b-7adf-47e3-865d-c86795f94989)




📸 **Memory**


![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/f43615b8-5457-4283-a50d-d4415fadae4b)
![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/a7a4d45b-e39c-4440-98ce-bcdb5112c5a0)
![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/512e2e16-9223-4613-a3a4-29838602f4df)
![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/443b19a7-0de8-421d-83e9-115db8f3b3ca)





💬 **Message**


![image](https://github.com/syeongkim/Mad-Camp-Week1/assets/107764281/0ec9020d-f905-4885-b0b9-a7fdf40ade1a)



### Beta

---

**apk link**

https://drive.google.com/file/d/1ILuw6a_f-wFjA5YoyFc4Du7-vdhzdSi2/view?usp=sharing
