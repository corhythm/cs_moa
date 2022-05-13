# CS모아(Convenience Store 모아)

> **대한민국의 주요 편의점 상품을 한 곳에 모아😋**

- 편의점 제품에 대한 리뷰 게시판을 제공합니다. 해당 게시판에 본인이 소비한 편의점 제품에 대한 리뷰를 남길 수 있습니다. 다른 유저들은 해당 리뷰에 대해 댓글을 달 수 있습니다.
- CU, GS25, Seven-Eleven, Ministop, Emart24 편의점의 이번 달 행사상품을 실시간으로 제공합니다.
- 사용자의 위치 기반으로 본인에게 가장 가까운 편의점을 알려줍니다.
- 편의점 상품으로 만든 레시피 게시판을 제공합니다. 사용자들은 해당 게시판에서 본인이 편의점 상품을 조합해 만든 음식 레시피 상세 정보를 업로드할 수 있습니다.

---

## 프로젝트 상세정보

- 이메일: dnr2144@gmail.com
- 개발 기간: 2021.09.03 ~ 2021.11.25
- Github
  - Client: [https://github.com/dnr2144/cs_moa](https://github.com/dnr2144/cs_moa)-back
  - Backend: [https://github.com/dnr2144/csmoa-back](https://github.com/dnr2144/csmoa-back)

## 애플리케이션 버전

- **Android**
  - **Kotlin: 1.5.10**
  - **Gradle: 7.0.3**
  - **Minimum SDK: 26**
  - **Target SDK: 30**
  - **Java: 1.8 SE**
  - **Kotlin: 1.5.10**
  - **Android Studio: 4.2.1**
- **SpringBoot**
  - **SpringBoot: 2.5.6**
  - **Java: 11**
  - **Gradle:**
- **Django: 3.2.7**
- **MySQL: 8.0.26**

## 기술스택

- **Client**
  - **Android**
  - **Retrofit(Network Communication)**
  - **Kotlin**
  - **JUnit(Test)**
  - **Coroutine**
  - **ViewModel + LiveData(MVVM)**
  - **Data Store(Local Device NoSQL)**
  - **SQLite3(Local Device RDB)**
  - **Kakao Map**
- **Backend**
  - **Nginx(Reverse Proxy Server)**
  - **Junit(Test)**
  - **MySQL(RDB)**
  - **Spring Boot(API Server)**
    - **JDBC Template**
    - **Firebase Storage**
    - **OAuth2.0 + JWT(Login)**
  - **Django**
    - **Selenium**
    - **Beautiful Soup**

> **본 프로젝트에서는 안드로이드 비즈니스 로직 개발 / API Server 개발 / Infra / 기획 및 설계를 담당했습니다.**

## 프로젝트 구조

> **프로젝트 구조는 다음과 같습니다.**

![Untitled](https://user-images.githubusercontent.com/43941383/145292511-6e0f9b5b-3dcc-443e-84fd-b58cc3e5cd3a.png)

## Django (Crawling Server)

---

> **Github Action에서 제공하는 Django Server를 이용해, CU, GS25, Seven-Eleven, Ministop, Emart24 웹 페이지에서 행사 상품 정보를 날마다 가져온 후 가공해서 RDB에 넣는다.**

- Github Action의 Cron 기능을 사용해서 매일 9시마다 5개 편의점에서 데이터를 가져와서 가공한 후, MySQL RDB에 업데이트됩니다.

## Spring Boot (API Server)

---

> **Android Application에서 요청한 데이터를 JSON으로 response한다.**

구조는 다음과 같습니다.

- Config: project congifuration을 관리.
- BaseException: custom exception message를 관리한다.
- Util: 자주 사용하는 util 기능을 모듈화해 관리한다.
- API
  - Controller: API를 관리.
  - Service: domain에 정의한 비즈니스 로직 호출 순서를 관리.
  - repository: domain + JDBC Template을 관리.
  - DTO: request/response dto를 관리.

## OAuth2.0 + JWT (Login)

---

> **구글/카카오 oauth provider를 사용해 불필요한 회원가입 프로세스를 제거. JWT Token을 사용해 Authorization Header 기반 인증 시스템을 구현.**

구조는 다음과 같습니다.

![Untitled 1](https://user-images.githubusercontent.com/43941383/145292403-c9a64569-5534-4e04-8c41-c3ebb43a20da.png)

- **이름/이메일/사진 3가지 정보만 oauth provider에 요청해, token 유출에 따른 보안 문제를 최소화했습니다.**

## 1. 로그인 / 회원가입

---

> **로컬 로그인, 구글 로그인, 카카오 로그인을 할 수 있습니다. 로컬 로그인을 할 경우에는 회원가입을 해야합니다.**

![Untitled 2](https://user-images.githubusercontent.com/43941383/145292499-11c66d2c-6003-4f30-ac74-eb04acccce63.png)

# 2. 제품 리뷰 게시판 / 리뷰 작성 / 리뷰 상세정보 / 댓글 / 대댓글

---

> **사용자가 편의점 제품을 리뷰를 작성하고, 해당 리뷰에 댓글 및 대댓글을 달 수 있는 제품 리뷰 게시판입니다.**

![Untitled 3](https://user-images.githubusercontent.com/43941383/145292501-c28fb768-6b31-4d87-81aa-bcb44efad45f.png)

## 3. 행사 상품 게시판 / 행사 상품 상세정보

---

> **현재 대한민국 주요 편의점에서 행사 하고 있는 제품 리스트를 보여주고, 상세 정보 페이지에서는 해당 제품 상세 정보 및 비슷한 제품 추천 서비스를 제공합니다.**

![Untitled 4](https://user-images.githubusercontent.com/43941383/145292503-ddbd1481-6ab3-4ddc-a147-b533dee15342.png)

## 4. 꿀조합 레시피

---

> **편의점에서 파는 제품을 조합해 만든 레시피를 다른 유저와 공유할 수 있는 꿀조합 레시피 게시판입니다.**

![Untitled 5](https://user-images.githubusercontent.com/43941383/145292506-83deac2e-944b-46b1-adf6-af9fba123a00.png)

## 5. 프로필 정보 수정

---

> **사용자 프로필 정보를 수정할 수 있습니다. 보안을 위해 수정할 수 있는 정보는 프로필 이미지와 닉네임으로 한정했습니다.**

![Untitled 6](https://user-images.githubusercontent.com/43941383/145292509-0cc4724d-75fd-4d72-b01a-0f17b182f3b0.png)
