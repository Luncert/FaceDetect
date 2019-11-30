import Axios from "axios";
import config from './Config.json';

export default Axios.create({
    baseURL: config.server.addr,
});