from socket import *
import logging

logging.basicConfig(level=logging.DEBUG, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

INT_SZ = 4


# little endian
def bytes_int(buf, start_pos, sz):
    v = 0
    for i in range(sz):
        v += (buf[start_pos] << (i * 8))
        start_pos += 1
    return v


def on_event(event_name):
    def inner(func):
        EventServer.listeners[event_name] = func
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

    def __receive_event(self, cli):
        # read event name's size
        buf = self.__read_n(cli, INT_SZ)
        if len(buf) == 0:
            return None
        name_sz = bytes_int(buf, 0, INT_SZ)
        if name_sz > 0:
            # read event name
            evt_name = self.__read_n(cli, name_sz).decode('utf-8')
        else:
            logger.error("Event name size should be positive, but 0 is read.")
            return None
        # read message size
        buf = self.__read_n(cli, INT_SZ)
        if len(buf) == 0:
            return None
        msg_sz = bytes_int(buf, 0, INT_SZ)
        msg = ''
        if msg_sz > 0:
            # read message
            msg = self.__read_n(cli, msg_sz).decode('utf-8')
        return Event(evt_name, msg)

    @staticmethod
    def __read_n(cli, n):
        data = bytearray(0)
        while n > 0:
            # TODO: set timeout
            tmp = cli.recv(n)
            if len(tmp) == 0:
                break
            data.extend(tmp)
            n -= len(tmp)
        return data

    @staticmethod
    def __on_event(evt):
        logger.debug('Event{name=%s, message=%s}' % (evt.name, evt.msg))
        if evt.name in EventServer.listeners:
            listener = EventServer.listeners[evt.name]
            listener(evt.msg)


@on_event('test-event')
def __test_event_listener(msg):
    print(msg)


if __name__ == '__main__':
    EventServer('localhost', 10901).start('stop')
