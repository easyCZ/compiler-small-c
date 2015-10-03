import glob
import re

SCOREBOARD_DIR = 'scoreboard/'
FILE_TYPE = 'html'

def main():
    ranks = []
    for filename in glob.glob('%s*.%s' % (SCOREBOARD_DIR, FILE_TYPE)):
        with open(filename, 'r') as f:
            text = f.read()

            pass_count = len([m.start() for m in re.finditer('Pass', text)])
            ranks.append((filename, pass_count))

    ranks.sort(key=lambda x: x[1], reverse=True)
    for rank in ranks:
        print rank[0].replace(SCOREBOARD_DIR, '').replace('.%s' % FILE_TYPE, ''), rank[1]


if __name__ == "__main__":
    main()