import io.restassured.response.Response;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CRUDTests {
    @Test


    public void request()
    {
        //Minimum Post request - with default value for "public"
        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\" : \"LeraSupperTest\"," +
                        " \"files\": {\n" +
                        "\"hello_world\": {\n" +
                        "\"content\": \"Hi\"\n" +
                        "}\n" +
                        "}}").
                when().
                post("https://api.github.com/gists").
                then().
                contentType("application/json; charset=utf-8").
                body("description", equalTo("LeraSupperTest"),"public", equalTo(false),"files.hello_world.content", equalTo("Hi")).and().headers("status", equalTo("201 Created"));



        //Minimum Post request - doesn't match without "content"

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\" : \"LeraSupperTest\"," +
                        " \"files\": {\n" +
                        "\"hello_world\": {\n" +
                        "}\n" +
                        "}}").
                when().
                post("https://api.github.com/gists").
                then().
                contentType("application/json; charset=utf-8").
                body("message", equalTo("Invalid request.\n\n\"content\" wasn't supplied.")).and().headers("status", equalTo("422 Unprocessable Entity"));



        //Minimum Post request - doesn't match without "file name"

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\" : \"LeraSupperTest\"," +
                        " \"files\": {\n" +
                        "}}").
                when().
                post("https://api.github.com/gists").
                then().
                contentType("application/json; charset=utf-8").
                body("message", equalTo("Validation Failed")).and().headers("status", equalTo("422 Unprocessable Entity"));

        //Unauthorized Post request - fails

        given().

                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\" : \"LeraSupperTest\"," +
                        " \"files\": {\n" +
                        "\"hello_world\": {\n" +
                        "\"content\": \"Hi\"\n" +
                        "}\n" +
                        "}}").
                when().
                post("https://api.github.com/gists").
                then().
                contentType("application/json; charset=utf-8").
                body("message", equalTo("Requires authentication")).and().headers("status", equalTo("401 Unauthorized"));


        //Invalid Authorization Post request - fails

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWc=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\" : \"LeraSupperTest\"," +
                        " \"files\": {\n" +
                        "\"hello_world\": {\n" +
                        "\"content\": \"Hi\"\n" +
                        "}\n" +
                        "}}").
                when().
                post("https://api.github.com/gists").
                then().
                contentType("application/json; charset=utf-8").
                body("message", equalTo("Bad credentials")).and().headers("status", equalTo("401 Unauthorized"));

        // Post request with few files, and files with the same name doesn't create twice
        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{\"description\": \"LeraTest\",\n" +
                        "  \"public\": true,\n" +
                        "  \"files\": {\n" +
                        "    \"SameName\": {\n" +
                        "      \"content\": \"Hey\"\n" +
                        "    },\n" +
                        "    \"SameName\": {\n" +
                        "      \"content\": \"Hallo\"\n" +
                        "    },\n" +
                        "    \"New\": {\n" +
                        "      \"content\": \"New\"\n" +
                        "    },\n" +
                        "    \"Another\": {\n" +
                        "      \"content\": \"Another\"\n" +
                        "    }\n" +
                        "  }}").
                when().
                post("https://api.github.com/gists").
                then().
                contentType("application/json; charset=utf-8").
                body("description", equalTo("LeraTest"),"public", equalTo(true),"files.SameName.content", equalTo("Hallo"),"files.Another.content", equalTo("Another"),"files.New.content", equalTo("New")).and().headers("status", equalTo("201 Created"));

        //Post request for creation gist for Patch/Get/Delete
        String myId;
        String myNode;
        String created;
        String updated;
        Response myResponse =
                given().
                        header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                        header("X-github-otp", "OTP").
                        header("Content-Type", "application/json").
                        body("{ \"description\" : \"LeraCrudTest\"," +
                                " \"files\": {\n" +
                                "\"hello_world\": {\n" +
                                "\"content\": \"Hi\"\n" +
                                "}\n" +
                                "}}").
                        when().
                        post("https://api.github.com/gists");
        myId = myResponse.then().contentType("application/json; charset=utf-8").extract().path("id");
        myNode = myResponse.then().contentType("application/json; charset=utf-8").extract().path("node_id");
        created = myResponse.then().contentType("application/json; charset=utf-8").extract().path("created_at");
        updated = myResponse.then().contentType("application/json; charset=utf-8").extract().path("updated_at");

        //Get without "Authorization" - return full response

        given().
                when().
                get("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("url",equalTo("https://api.github.com/gists/" + myId),
                        "forks_url",equalTo("https://api.github.com/gists/" + myId + "/forks"),
                        "commits_url",equalTo("https://api.github.com/gists/" + myId + "/commits"),
                        "id", equalTo(myId),
                        "node_id", equalTo(myNode),
                        "git_pull_url",equalTo("https://gist.github.com/" + myId + ".git"),
                        "git_push_url",equalTo("https://gist.github.com/" + myId + ".git"),
                        "html_url",equalTo("https://gist.github.com/" + myId),
                        "files.hello_world.filename", equalTo("hello_world"),
                        "files.hello_world.type", equalTo("text/plain"),
                        "files.hello_world.filename", equalTo("hello_world"),
                        "files.hello_world.language", equalTo(null),
                        "files.hello_world.size", equalTo(2),
                        "truncated", equalTo(false),
                        "files.hello_world.content", equalTo("Hi"),
                        "public", equalTo(false),
                        "created_at", equalTo(created),
                        "updated_at", equalTo(updated),
                        "description", equalTo("LeraCrudTest"),
                        "comments", equalTo(0),
                        "user", equalTo(null),
                        "comments_url", equalTo("https://api.github.com/gists/" + myId + "/comments"),
                        "owner.login", equalTo("StarikovaLera"),
                        "owner.id", equalTo(53900032),
                        "owner.node_id", equalTo("MDQ6VXNlcjUzOTAwMDMy"),
                        "owner.avatar_url", equalTo("https://avatars1.githubusercontent.com/u/53900032?v=4"),
                        "owner.gravatar_id", equalTo(""),
                        "owner.url", equalTo("https://api.github.com/users/StarikovaLera"),
                        "owner.html_url", equalTo("https://github.com/StarikovaLera"),
                        "owner.followers_url", equalTo("https://api.github.com/users/StarikovaLera/followers"),
                        "owner.following_url", equalTo("https://api.github.com/users/StarikovaLera/following{/other_user}"),
                        "owner.gists_url", equalTo("https://api.github.com/users/StarikovaLera/gists{/gist_id}"),
                        "owner.starred_url", equalTo("https://api.github.com/users/StarikovaLera/starred{/owner}{/repo}"),
                        "owner.subscriptions_url", equalTo("https://api.github.com/users/StarikovaLera/subscriptions"),
                        "owner.organizations_url", equalTo("https://api.github.com/users/StarikovaLera/orgs"),
                        "owner.repos_url", equalTo("https://api.github.com/users/StarikovaLera/repos"),
                        "owner.events_url", equalTo("https://api.github.com/users/StarikovaLera/events{/privacy}"),
                        "owner.received_events_url", equalTo("https://api.github.com/users/StarikovaLera/received_events"),
                        "owner.type", equalTo("User"),
                        "owner.site_admin", equalTo(false),
                        "forks[0]", equalTo(null),
                        "history.user[0].login", equalTo("StarikovaLera"),
                        "history.user[0].id", equalTo(53900032),
                        "history.user[0].node_id", equalTo("MDQ6VXNlcjUzOTAwMDMy"),
                        "history.user[0].avatar_url", equalTo("https://avatars1.githubusercontent.com/u/53900032?v=4"),
                        "history.user[0].gravatar_id", equalTo(""),
                        "history.user[0].url", equalTo("https://api.github.com/users/StarikovaLera"),
                        "history.user[0].html_url", equalTo("https://github.com/StarikovaLera"),
                        "history.user[0].followers_url", equalTo("https://api.github.com/users/StarikovaLera/followers"),
                        "history.user[0].following_url", equalTo("https://api.github.com/users/StarikovaLera/following{/other_user}"),
                        "history.user[0].gists_url", equalTo("https://api.github.com/users/StarikovaLera/gists{/gist_id}"),
                        "history.user[0].starred_url", equalTo("https://api.github.com/users/StarikovaLera/starred{/owner}{/repo}"),
                        "history.user[0].subscriptions_url", equalTo("https://api.github.com/users/StarikovaLera/subscriptions"),
                        "history.user[0].organizations_url", equalTo("https://api.github.com/users/StarikovaLera/orgs"),
                        "history.user[0].repos_url", equalTo("https://api.github.com/users/StarikovaLera/repos"),
                        "history.user[0].events_url", equalTo("https://api.github.com/users/StarikovaLera/events{/privacy}"),
                        "history.user[0].received_events_url", equalTo("https://api.github.com/users/StarikovaLera/received_events"),
                        "history.user[0].type", equalTo("User"),
                        "history.user[0].site_admin", equalTo(false),
                        "truncated", equalTo(false)
                ).and().headers("status", equalTo("200 OK"));

        //Patch request - doesn't rewrite read only fields
        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{\"url\": \"https://api.github.com\",\n" +
                        "\"forks_url\": \"https://api.github.com\",\n" +
                        "\"commits_url\": \"https://api.github.com\",\n" +
                        "\"id\": \"123\",\n" +
                        "\"node_id\": \"123\",\n" +
                        "\"git_pull_url\": \"https://api.github.com\",\n" +
                        "\"git_push_url\": \"https://api.github.com\",\n" +
                        "\"html_url\": \"https://api.github.com\",\n" +
                        "\"files\":{\n" +
                        "\"hello_world\":{\n" +
                        "\"filename\": \"hello_world\",\n" +
                        "\"type\": \"plain\",\n" +
                        "\"language\": \"English\",\n" +
                        "\"raw_url\": \"https://api.github.com\",\n" +
                        "\"size\": 333,\n" +
                        "\"truncated\": true,\n" +
                        "\"content\": \"Hi\"\n" +
                        "}\n" +
                        "},\n" +
                        "\"public\": false,\n" +
                        "\"created_at\": \"2017-08-13T08:23:44Z\",\n" +
                        "\"description\": \"LeraCrudTest\",\n" +
                        "\"comments\": 123,\n" +
                        "\"user\": 123,\n" +
                        "\"comments_url\": \"https://api.github.com\",\n" +
                        "\"owner\":{\n" +
                        "  \"login\": \"Lera\",\n" +
                        "\"id\": 123,\n" +
                        "\"node_id\": \"123\",\n" +
                        "\"avatar_url\": \"https://api.github.com\",\n" +
                        "\"gravatar_id\": \"123\",\n" +
                        "\"url\": \"https://api.github.com\",\n" +
                        "\"html_url\": \"https://api.github.com\",\n" +
                        "\"followers_url\": \"https://api.github.com\",\n" +
                        "\"following_url\": \"https://api.github.com}\",\n" +
                        "\"gists_url\": \"https://api.github.com\",\n" +
                        "\"starred_url\": \"https://api.github.com\",\n" +
                        "\"subscriptions_url\": \"https://api.github.com\",\n" +
                        "\"organizations_url\": \"https://api.github.com\",\n" +
                        "\"repos_url\": \"https://api.github.com\",\n" +
                        "\"events_url\": \"https://api.github.com\",\n" +
                        "\"received_events_url\": \"https://api.github.com\",\n" +
                        "\"type\": \"Admin\",\n" +
                        "\"site_admin\": true},\n" +
                        "\n" +
                        "\"truncated\": true}").
                when().
                patch("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("url",equalTo("https://api.github.com/gists/" + myId),
                        "forks_url",equalTo("https://api.github.com/gists/" + myId + "/forks"),
                        "commits_url",equalTo("https://api.github.com/gists/" + myId + "/commits"),
                        "id", equalTo(myId),
                        "node_id", equalTo(myNode),
                        "git_pull_url",equalTo("https://gist.github.com/" + myId + ".git"),
                        "git_push_url",equalTo("https://gist.github.com/" + myId + ".git"),
                        "html_url",equalTo("https://gist.github.com/" + myId),
                        "files.hello_world.filename", equalTo("hello_world"),
                        "files.hello_world.type", equalTo("text/plain"),
                        "files.hello_world.filename", equalTo("hello_world"),
                        "files.hello_world.language", equalTo(null),
                        "files.hello_world.size", equalTo(2),
                        "truncated", equalTo(false),
                        "files.hello_world.content", equalTo("Hi"),
                        "public", equalTo(false),
                        "created_at", equalTo(created),
                        "description", equalTo("LeraCrudTest"),
                        "comments", equalTo(0),
                        "user", equalTo(null),
                        "comments_url", equalTo("https://api.github.com/gists/" + myId + "/comments"),
                        "owner.login", equalTo("StarikovaLera"),
                        "owner.id", equalTo(53900032),
                        "owner.node_id", equalTo("MDQ6VXNlcjUzOTAwMDMy"),
                        "owner.avatar_url", equalTo("https://avatars1.githubusercontent.com/u/53900032?v=4"),
                        "owner.gravatar_id", equalTo(""),
                        "owner.url", equalTo("https://api.github.com/users/StarikovaLera"),
                        "owner.html_url", equalTo("https://github.com/StarikovaLera"),
                        "owner.followers_url", equalTo("https://api.github.com/users/StarikovaLera/followers"),
                        "owner.following_url", equalTo("https://api.github.com/users/StarikovaLera/following{/other_user}"),
                        "owner.gists_url", equalTo("https://api.github.com/users/StarikovaLera/gists{/gist_id}"),
                        "owner.starred_url", equalTo("https://api.github.com/users/StarikovaLera/starred{/owner}{/repo}"),
                        "owner.subscriptions_url", equalTo("https://api.github.com/users/StarikovaLera/subscriptions"),
                        "owner.organizations_url", equalTo("https://api.github.com/users/StarikovaLera/orgs"),
                        "owner.repos_url", equalTo("https://api.github.com/users/StarikovaLera/repos"),
                        "owner.events_url", equalTo("https://api.github.com/users/StarikovaLera/events{/privacy}"),
                        "owner.received_events_url", equalTo("https://api.github.com/users/StarikovaLera/received_events"),
                        "owner.type", equalTo("User"),
                        "owner.site_admin", equalTo(false),
                        "forks[0]", equalTo(null),
                        "history.user[0].login", equalTo("StarikovaLera"),
                        "history.user[0].id", equalTo(53900032),
                        "history.user[0].node_id", equalTo("MDQ6VXNlcjUzOTAwMDMy"),
                        "history.user[0].avatar_url", equalTo("https://avatars1.githubusercontent.com/u/53900032?v=4"),
                        "history.user[0].gravatar_id", equalTo(""),
                        "history.user[0].url", equalTo("https://api.github.com/users/StarikovaLera"),
                        "history.user[0].html_url", equalTo("https://github.com/StarikovaLera"),
                        "history.user[0].followers_url", equalTo("https://api.github.com/users/StarikovaLera/followers"),
                        "history.user[0].following_url", equalTo("https://api.github.com/users/StarikovaLera/following{/other_user}"),
                        "history.user[0].gists_url", equalTo("https://api.github.com/users/StarikovaLera/gists{/gist_id}"),
                        "history.user[0].starred_url", equalTo("https://api.github.com/users/StarikovaLera/starred{/owner}{/repo}"),
                        "history.user[0].subscriptions_url", equalTo("https://api.github.com/users/StarikovaLera/subscriptions"),
                        "history.user[0].organizations_url", equalTo("https://api.github.com/users/StarikovaLera/orgs"),
                        "history.user[0].repos_url", equalTo("https://api.github.com/users/StarikovaLera/repos"),
                        "history.user[0].events_url", equalTo("https://api.github.com/users/StarikovaLera/events{/privacy}"),
                        "history.user[0].received_events_url", equalTo("https://api.github.com/users/StarikovaLera/received_events"),
                        "history.user[0].type", equalTo("User"),
                        "history.user[0].site_admin", equalTo(false),
                        "truncated", equalTo(false)
                ).and().headers("status", equalTo("200 OK"));

        //Minimum Patch request - possible to rewrite "description"
        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\":\"Update\"}").
                when().
                patch("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("description", equalTo("Update"),"id", equalTo(myId),"files.hello_world.content", equalTo("Hi")).and().headers("status", equalTo("200 OK"));


        //Patch request - "public" impossible to rewrite

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\":\"Update\",\n" +
                        "  \"public\": true}").
                when().
                patch("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("description", equalTo("Update"),"id", equalTo(myId),"public", equalTo(false)).and().headers("status", equalTo("200 OK"));

        //Unauthorized Patch request - fails

        given().
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\":\"Update\"}").
                when().
                patch("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("message", equalTo("Not Found")).and().headers("status", equalTo("404 Not Found"));

        //Patch request - adding new files

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\":\"Update\",\n" +
                        "  \"public\": true,\n" +
                        "  \"files\":{\n" +
                        "  \"ThanDelete\":{\n" +
                        "      \"content\":\"yes\"}\n" +
                        "  }}").
                when().
                patch("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("files.hello_world.content", equalTo("Hi"), "files.ThanDelete.content", equalTo("yes"),"id", equalTo(myId)).and().headers("status", equalTo("200 OK"));

        //Patch request - editing file content

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{ \"description\":\"Update\",\n" +
                        "  \"public\": true,\n" +
                        "  \"files\":{\n" +
                        "  \"ThanDelete\":{\n" +
                        "      \"content\":\"no\"}\n" +
                        "  }}").
                when().
                patch("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("files.hello_world.content", equalTo("Hi"), "files.ThanDelete.content", equalTo("no"),"id", equalTo(myId)).and().headers("status", equalTo("200 OK"));

        //Patch request - delete file

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                body("{\"description\": \"Update\",\n" +
                        "  \"files\": {\n" +
                        "    \"ThanDelete\": null\n" +
                        "  }}").
                when().
                patch("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                body("files.hello_world.content", equalTo("Hi"), "files.ThanDelete.content", equalTo(null),"id", equalTo(myId)).and().headers("status", equalTo("200 OK"));

        //Delete gist request

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                when().
                delete("https://api.github.com/gists/" + myId).
                then().
                contentType("application/octet-stream").
                headers("status", equalTo("204 No Content"));

        //Delete the same gist request

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                when().
                delete("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                headers("status", equalTo("404 Not Found"));

        //Get deleted gist request

        given().
                header("Authorization", "Basic U3Rhcmlrb3ZhTGVyYTpPNjZra2hyZWk=").
                header("X-github-otp", "OTP").
                header("Content-Type", "application/json").
                when().
                get("https://api.github.com/gists/" + myId).
                then().
                contentType("application/json; charset=utf-8").
                headers("status", equalTo("404 Not Found"));


    }
}


