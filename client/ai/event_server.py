from socket import *
import logging

logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)


def on_event(event_name):
    def inner(func):
        if event_name not in EventServer.listeners:
            EventServer.listeners[event_name] = []
        EventServer.listeners[event_name].append(func)
    return inner


class Event(object):
    def __init__(self, name, msg):
        self.name = name
        self.msg = msg


STOP_EVENT = Event(None, None)


class EventServer(object):
    listeners = {}

    def __init__(self, host, port):
        self.host = host
        self.port = port

    def start(self, stop_event_name):
        s = socket()
        logger.info("EventServer started.")
        s.bind((self.host, self.port))
        s.listen(1)
        stop = False
        while not stop:
            cli, addr = s.accept()
            logger.info('Client connected from %s:%d.', addr[0], addr[1])
            while True:
                evt = self.__receive_event(cli)
                if not evt:
                    break
                elif len(evt.name) == len(stop_event_name) and evt.name.find(stop_event_name) == 0:
                    stop = True
                    break
                else:
                    self.__on_event(evt)
            logger.info('Client disconnected from %s:%d.', addr[0], addr[1])
            cli.close()
        logger.info("EventServer stopped.")
        s.close()

    @staticmethod
    def __receive_event(cli):
        # read event name's size
        ret = cli.recv(1)
        if len(ret) == 0:
            return None
        sz = ord(ret)
        if sz > 0:
            # read event name
            ret = cli.recv(sz)
            if len(ret) == 0:
                logger.debug("Opposite end closed.")
                return None
            elif len(ret) != sz:
                logger.error("Failed to read event name, expected sz=%d, read=%d." % (sz, len(ret)))
                return None
            evt_name = ret.decode('utf-8')
        else:
            logger.error("Event name size should be positive, but 0 is read.")
            return None
        # read message size
        ret = cli.recv(1)
        if len(ret) == 0:
            return None
        sz = ord(ret)
        msg = ''
        if sz > 0:
            # read message
            ret = cli.recv(sz)
            if len(ret) == 0:
                logger.debug("Opposite end closed.")
                return None
            elif len(ret) != sz:
                logger.error("Failed to read message, expected sz=%d, read=%d." % (sz, len(ret)))
                return None
            msg = ret.decode('utf-8')
        return Event(evt_name, msg)

    @staticmethod
    def __on_event(evt):
        logger.debug('Event{name=%s, message=%s}' % (evt.name, evt.msg))
        if evt.name in EventServer.listeners:
            listeners = EventServer.listeners[evt.name]
            for listener in listeners:
                listener(evt.msg)
