import Axios from "axios";
import config from '../Config.json';
const format = require('format') as any

export default Axios.create({
    baseURL: format('http://%s:%d', config.server.host, config.server.port),
});