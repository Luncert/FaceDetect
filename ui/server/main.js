

const express = require('express')
const cors = require('cors')
const app = express()

app.use('/static', express.static('server/static'))
app.use(cors())


// accessible even user is not authorized
app.get('/user/avatar/:account', (req, rep) => {
    rep.json({
        url: 'http://localhost:8000/static/avatar.jpg'
    })
})

app.get('/user/signin/:credential', async (req, rep) => {
    await sleep(2000);
    rep.json({
        identified: true
    })
})

app.listen(8000)