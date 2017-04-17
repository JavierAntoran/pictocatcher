import web
import sqlite3


urls = (
  '/', 'Index'
)

app = web.application(urls, globals())
render = web.template.render('templates/', base='layout')

class Index:
    def GET(self):
        greeting='foo'

        return render.index(greeting=greeting)

if __name__ == "__main__":
    app.run()