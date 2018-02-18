"""added_streaming_service_type

Revision ID: 43d6ac1bbd60
Revises: e5bc935fc6ca
Create Date: 2018-02-18 15:58:28.100928

"""

# revision identifiers, used by Alembic.
revision = '43d6ac1bbd60'
down_revision = 'e5bc935fc6ca'
branch_labels = None
depends_on = None

from alembic import op
import sqlalchemy as sa


def upgrade():
	op.add_column('song',sa.Column('streaming_service_type', sa.VARCHAR(), nullable=False, server_default='YOUTUBE'), schema='scalable')


def downgrade():
	op.drop_column('song', 'streaming_service_type', schema='scalable')
