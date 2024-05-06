const row = document.getElementById("row");

window.onload = async () => {
    let posts = await getPosts();
    console.log(posts)
    for (let i = 0; i < posts.length; i++) {
        console.log(posts[i])

        let div = createTag("div", "col-md-3", "card", "p-3");
        let h4 = createTag("h4", "text-primary");
        let p = createTag("p", "lead");

        h4.innerText = posts[i].title;
        p.innerText = posts[i].body;

        div.append(h4);
        div.append(p);

        row.append(div);
    }
}

async function getPosts() {
    let response = await fetch("https://jsonplaceholder.typicode.com/posts");
    return await response.json();
}

function createTag(tagName, ...className) {
    let tag = document.createElement(tagName);

    className.forEach(item => {
        tag.classList.add(item)
    })

    return tag;
}