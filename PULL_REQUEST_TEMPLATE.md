# Pull Request Template

Our modified pull request template based off of a [Quick Left blog post](http://quickleft.com/blog/pull-request-templates-make-code-review-easier).

## Usage

### Bookmarklet

Select the text below and drag into your bookmarks bar. When you have a pull request open, click the button and the template will be copied into place.

````javascript
javascript:(function() {var e = document.getElementById('pull_request_body');if (e) {if (e.value == '') {e.value += "#### What's this PR do?\n\n\n\n#### Where should the reviewer start?\n\n\n\n#### How should this be manually tested?\n\n- [ ]\n\n#### Any background context you want to provide?\n\n\n\n#### What are the relevant tickets?\n\n\n\n#### Screenshots:\n\n\n\n#### Questions:\n\n\n";}}})();
````

### Old Fashioned

Copy the template markdown below and paste it into your pull request description:

````markdown
#### What's this PR do?



#### How should this be manually tested?

- [ ]

#### Any background context you want to provide?



#### What are the relevant tickets?



#### Screenshots:



#### Questions:



````
