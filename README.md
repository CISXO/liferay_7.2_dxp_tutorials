## Liferay DXP 7.2 tutorials 
### OSGI framework

* Liferay portlet 구조는 mvc 패턴이 포틀릿 생명 주기에 맞춰 자동으로 처리됩니다. portlet 클래스가 뷰를 반환하지 않고, jsp에서 필요한 경로를 지정해 뷰를 결정합니다. 

* client Request -> Portlet -> Action Phase -> Render Phase -> JSP View로 동작합니다.

<p> 사용자가 url을 요청하면 portlet 클래스의 메서드에 의해 처리됩니다. portlet action을 처리하고 데이터를 업데이트하거나 처리 후 jsp에 대한 경로를 설정합니다. action이 완료되면 portlet은 자동으로 해당 jsp 페이지를 렌더링합니다. 지정된 jsp 파일이 렌더링되어 클라이언트에 응답합니다.</p>
<p>Liferay portlet은 url 요청을 처리할 때, 포틀릿의 메서드에서 mvcPath를 설정하여 자동으로 해당 jsp 페이지로 매핑됩니다. 즉, 포틀릿의 mvc 프레임워크는 요청 처리 후 어떤 jsp 페이지를 사용할 지 결정하고, 그 페이지를 사용자에게 반환합니다. 만약, mvcPath를 따로 설정하지 않을 경우 초기 @Component속성의 property의 view를 지정하면 이에 대해 렌더링됩니다.</p>


<h3>portlet mvc</h3>
<p>portlet의 model은 model, service, persistence로 Service Builder라는 계층 구조로 구성되어 있습니다. 이 서비스 빌더는 liferay의 객체 모델을 정의하고 sql db에 매핑하기 위한 코드 생성 도구입니다. service.xml 파일에 모델을 정의하면 객체 모델, 서비스 계층, 지속성 계층을 한번에 생성할 수 있고, 동시에 dxp가 지원하는 모든 db를 지원할 수 있습니다.</p>


* guestbook-api: 웹 모듈과 서비스 모듈은 이 모듈을 통해 공유 인터페이스를 정의
* guestbook-service: guestbook-api에 정의된 로직을 구현하며 데이터를 가져오거나 저장하는 백그라운드 작업을 처리
* guestbook-web: 사용자의 요청을 처리하고, 데이터를 표시하거나 수정할 때 guestbook-service에 서비스 호출을 통해 데이터를 처리합니다.

<p>Tomcat에서 배포: tomcat에서는 이 모듈들이 OSGI 번들로 배포됩니다. 각 번들은 모듈에 해당하며, 라이프레이는 이를 독립적으로 처리합니다. 배포가 된 후 guestbook-web 모듈은 사용자와 상호작용하며 그 과정에서 guestbook-service를 호출하여 데이터를 가져오거나 수정합니다.</p>
